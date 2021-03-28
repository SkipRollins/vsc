package com.github.skiprollins.vsc.network

import android.content.Context
import com.github.skiprollins.vsc.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MockApiInterceptor(
    private val context: Context,
    private val gson: Gson
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val endpoint = chain.request().url().toString()
        return if (endpoint.contains(BuildConfig.MOCK_API)) when {
            endpoint.contains("products") -> buildAllProductResponse(chain)

            endpoint.contains("product") -> buildSingleProductResponse(chain)

            else -> chain.proceed(chain.request())

        } else chain.proceed(chain.request())
    }

    private fun buildAllProductResponse(chain: Interceptor.Chain): Response {
        val body = ResponseBody.create(MediaType.parse("application/json"), getRawProductList())
        val response = Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .body(body)
            .request(chain.request())
            .message("OK")
            .code(200)
            .build()

        return response
    }

    private fun buildSingleProductResponse(chain: Interceptor.Chain): Response {
        val id = parseId(chain.request().url().toString())
        if (id.isNotEmpty()) {

            val product = getProductList().find { it.id == id }

            if (product != null) {
                val json = gson.toJson(product)

                val body = ResponseBody.create(MediaType.parse("application/json"), json)
                val response = Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .body(body)
                    .request(chain.request())
                    .message("OK")
                    .code(200)
                    .build()

                return response

            } else {
                val response = Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .request(chain.request())
                    .message("Not Found")
                    .code(404)
                    .build()

                return response
            }

        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun parseId(url: String): String {
        val start = url.indexOf('?')

        if (start < 0) return ""

        val queries = url.substring(start + 1).split("&")
        val id = queries
            .map {
                val keyPair = it.split("=")
                Pair(keyPair[0], keyPair[1])
            }.filter { it.first == "id" }
            .map { it.second }
            .firstOrNull()

        return id ?: ""
    }

    private fun getProductList(): List<Product> {
        val json = getRawProductList()

        val productListType = (object: TypeToken<ArrayList<Product>>(){}).type
        val list: List<Product> = gson.fromJson(json, productListType)
        return list
    }

    private fun getRawProductList(): String {
        val stream = context.resources?.assets?.open("data.json")
        val reader = BufferedReader(InputStreamReader(stream))
        val json = StringBuilder()

        while (true) {

            val line = try {
                reader.readLine() ?: break

            } catch (exception: IOException) {
                break
            }

            json.append(line)
        }

        return json.toString()
    }
}