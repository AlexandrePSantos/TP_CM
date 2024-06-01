import com.example.trabpratico.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api-4pd81iv2o-alexandres-projects-d97edc96.vercel.app/api/cm/"

    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()

        // Adiciona um interceptor para adicionar o token de autenticação aos cabeçalhos de cada solicitação
        builder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Adiciona o token de autenticação aos cabeçalhos se estiver disponível
            authToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        // Adiciona um interceptor para registrar as solicitações e respostas HTTP no logcat
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        builder.build()
    }

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
