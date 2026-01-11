# API Documentation & Examples

**Last Updated:** 2026-01-11
**Milestone:** 5 Complete
**API:** Fixer.io Currency Exchange Rates

---

## Table of Contents

1. [API Overview](#api-overview)
2. [Base Configuration](#base-configuration)
3. [API Endpoints](#api-endpoints)
4. [Request/Response Examples](#requestresponse-examples)
5. [Error Handling](#error-handling)
6. [Rate Limiting](#rate-limiting)
7. [Integration Examples](#integration-examples)
8. [Testing API](#testing-api)

---

## API Overview

### Service Details

| Property | Value |
|----------|-------|
| **Service** | Fixer.io Currency Exchange Rates |
| **API Type** | REST (Representational State Transfer) |
| **Protocol** | HTTPS |
| **Base URL** | `https://api.fixer.io` |
| **Authentication** | API Key (free tier available) |
| **Response Format** | JSON |
| **Rate Limit** | 100 requests/month (free) |
| **Documentation** | https://fixer.io/documentation |

### Current Project Integration

**Project Configuration:**
- **Module:** `restapi`
- **Client:** Retrofit 2.9.0 + OkHttp 4.12.0
- **Serialization:** Gson 2.10.1
- **Kotlin:** Coroutines with suspend functions

---

## Base Configuration

### Retrofit Setup (RestApi.kt)

```kotlin
// Location: restapi/src/main/java/io/github/wawakaka/restapi/RestApi.kt

object RestApi {

    private const val BASE_URL = "https://api.fixer.io/"
    private const val TIMEOUT_SECONDS = 60L

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }
}

// Interceptor for common headers
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Add custom headers if needed
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
```

### API Service Interface (CurrencyRatesApi.kt)

```kotlin
// Location: repository/src/main/java/io/github/wawakaka/repository/currencyrates/CurrencyRatesApi.kt

interface CurrencyRatesApi {

    /**
     * Get latest exchange rates for a given base currency
     *
     * @param base The base currency (ISO 4217 code, e.g., "EUR")
     * @return Response containing exchange rates
     */
    @GET("latest")
    suspend fun getLatestWithBase(
        @Query("base") base: String
    ): CurrencyRatesResponse

    /**
     * Get historical rates for a specific date
     *
     * @param date Date in YYYY-MM-DD format
     * @param base Base currency
     * @return Response containing rates for specified date
     */
    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("base") base: String
    ): CurrencyRatesResponse
}
```

---

## API Endpoints

### Endpoint 1: Latest Rates

**GET** `/latest`

Returns the most recent exchange rates for a given base currency.

**Parameters:**
```
Query Parameters:
- base (string, optional): Base currency code (default: "EUR")
  - Example: "USD", "GBP", "JPY"
  - ISO 4217 format
```

**Example Requests:**

```bash
# Get rates with EUR as base (default)
curl "https://api.fixer.io/latest"

# Get rates with USD as base
curl "https://api.fixer.io/latest?base=USD"

# Get rates with GBP as base
curl "https://api.fixer.io/latest?base=GBP"
```

**Response:**
```json
{
  "base": "EUR",
  "date": "2024-01-11",
  "rates": {
    "AUD": 1.6524,
    "BGN": 1.9558,
    "BRL": 5.2969,
    "CAD": 1.4664,
    "CHF": 0.9357,
    "CNY": 7.8456,
    "CZK": 24.5234,
    "DKK": 7.4608,
    "GBP": 0.8736,
    "HKD": 8.5456,
    "HRK": 7.5345,
    "HUF": 365.2145,
    "IDR": 16854.2568,
    "ILS": 3.9856,
    "INR": 89.3456,
    "ISK": 134.5678,
    "JPY": 154.3456,
    "KRW": 1380.2456,
    "MXN": 17.2345,
    "MYR": 5.0234,
    "NOK": 11.5678,
    "NZD": 1.8456,
    "PHP": 60.2345,
    "PLN": 4.2345,
    "RON": 4.9856,
    "RUB": 98.3456,
    "SEK": 11.2345,
    "SGD": 1.3456,
    "THB": 37.2345,
    "TRY": 32.4567,
    "TWD": 32.4567,
    "USD": 1.0892,
    "ZAR": 20.3456
  }
}
```

### Endpoint 2: Historical Rates

**GET** `/{date}`

Returns exchange rates for a specific historical date.

**Parameters:**
```
Path Parameters:
- date (string, required): Date in YYYY-MM-DD format
  - Example: "2024-01-01", "2023-12-25"

Query Parameters:
- base (string, optional): Base currency code (default: "EUR")
```

**Example Requests:**

```bash
# Get rates for specific date with EUR base
curl "https://api.fixer.io/2024-01-01"

# Get rates for specific date with USD base
curl "https://api.fixer.io/2024-01-01?base=USD"
```

**Response:** Same structure as latest rates endpoint

---

## Request/Response Examples

### Data Models

#### CurrencyRatesResponse

```kotlin
// Location: repository/src/main/java/io/github/wawakaka/repository/currencyrates/model/response/CurrencyRatesResponse.kt

data class CurrencyRatesResponse(
    @SerializedName("base")
    val base: String?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("rates")
    val rates: Rates?
)

typealias Rates = Map<String, Double>
```

### Example Usage in Repository

```kotlin
// Location: repository/src/main/java/io/github/wawakaka/repository/currencyrates/CurrencyRatesRepository.kt

class CurrencyRatesRepository(
    private val api: CurrencyRatesApi
) {
    /**
     * Get the latest currency exchange rates
     *
     * @param base Base currency (default: EUR)
     * @return List of currency code to rate pairs
     * @throws Exception if API call fails
     */
    suspend fun getLatestCurrencyRates(base: String = "EUR"): CurrencyRatesResponse {
        return try {
            api.getLatestWithBase(base)
        } catch (e: Exception) {
            Log.e("CurrencyRatesRepo", "Failed to fetch rates", e)
            throw e
        }
    }

    /**
     * Get historical rates for a specific date
     *
     * @param date Date in YYYY-MM-DD format
     * @param base Base currency
     * @return Exchange rates for the date
     */
    suspend fun getHistoricalRates(
        date: String,
        base: String = "EUR"
    ): CurrencyRatesResponse {
        return api.getHistoricalRates(date, base)
    }
}
```

### Example Usage in Use Case

```kotlin
// Location: domain/src/main/java/io/github/wawakaka/domain/usecase/GetLatestRatesUsecase.kt

class GetLatestRatesUsecase(
    private val repository: CurrencyRatesRepository
) {
    /**
     * Get latest currency rates as list of pairs
     *
     * @return List of (currencyCode, rate) pairs
     */
    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        return try {
            val response = repository.getLatestCurrencyRates("EUR")
            response.rates?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e("GetLatestRatesUsecase", "Error: ${e.message}")
            throw e
        }
    }
}
```

### Example Usage in Presenter

```kotlin
// Location: app/src/main/java/io/github/wawakaka/basicframeworkproject/presentation/content/CurrencyPresenter.kt

class CurrencyPresenter(
    private val usecase: GetLatestRatesUsecase
) : BasePresenter<CurrencyContract.View>(), CurrencyContract.Presenter {

    override fun onButtonClickedEvent() {
        presenterScope.launch {
            view?.showLoading()
            try {
                val rates = usecase.getLatestCurrencyRates()
                view?.onGetDataSuccess(rates)
            } catch (e: Exception) {
                view?.onGetDataFailed(e)
            } finally {
                view?.hideLoading()
            }
        }
    }
}
```

### Example Usage in Composable

```kotlin
// Location: app/src/main/java/io/github/wawakaka/basicframeworkproject/presentation/screens/CurrencyScreen.kt

@Composable
fun CurrencyScreen(
    presenter: CurrencyPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var currencyRates by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val view = object : CurrencyContract.View {
        override fun showLoading() {
            isLoading = true
        }

        override fun hideLoading() {
            isLoading = false
        }

        override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
            currencyRates = data
            errorMessage = null
        }

        override fun onGetDataFailed(throwable: Throwable) {
            errorMessage = throwable.message ?: "Unknown error"
        }
    }

    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.onButtonClickedEvent()  // This calls the API
    }

    DisposableEffect(Unit) {
        onDispose { presenter.detach() }
    }

    CurrencyContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        currencyRates = currencyRates,
        onRefresh = { presenter.onButtonClickedEvent() },
        modifier = modifier
    )
}
```

---

## Error Handling

### HTTP Error Codes

| Code | Meaning | Handling |
|------|---------|----------|
| **200** | OK | Request successful, process response |
| **400** | Bad Request | Invalid parameters (e.g., wrong base currency) |
| **401** | Unauthorized | Invalid or missing API key |
| **403** | Forbidden | Request not allowed (rate limit or IP) |
| **404** | Not Found | Endpoint or date doesn't exist |
| **429** | Too Many Requests | Rate limit exceeded |
| **500** | Server Error | API server error, retry later |
| **503** | Service Unavailable | API temporarily down |

### Exception Handling

```kotlin
// In Presenter
override fun onButtonClickedEvent() {
    presenterScope.launch {
        view?.showLoading()
        try {
            val rates = usecase.getLatestCurrencyRates()
            view?.onGetDataSuccess(rates)
        } catch (e: HttpException) {
            // HTTP-specific error
            val errorMessage = when (e.code()) {
                400 -> "Invalid currency code"
                401 -> "Unauthorized - check API key"
                429 -> "Rate limit exceeded, try again later"
                500 -> "Server error, try again later"
                else -> "HTTP Error: ${e.code()}"
            }
            view?.onGetDataFailed(Exception(errorMessage))
        } catch (e: IOException) {
            // Network error
            view?.onGetDataFailed(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            // Other errors
            view?.onGetDataFailed(Exception("Error: ${e.message}"))
        } finally {
            view?.hideLoading()
        }
    }
}
```

### UI Error Display

```kotlin
@Composable
fun CurrencyScreen(
    presenter: CurrencyPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    // ... state declarations ...

    when {
        isLoading -> LoadingIndicator(modifier)
        errorMessage != null -> ErrorMessage(
            message = errorMessage!!,
            onRetry = { presenter.onButtonClickedEvent() },
            modifier = modifier
        )
        else -> CurrencyContent(currencyRates, modifier)
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
```

---

## Rate Limiting

### Free Tier Limits

```
Monthly Quota: 100 API requests
Rate: Approximately 3 requests per day

Recommended Strategy:
1. Cache results locally (database)
2. Request fresh data once per day
3. Use cached data for subsequent requests
4. Implement retry logic with exponential backoff
```

### Rate Limit Handling

```kotlin
// Future enhancement for caching
interface CacheStrategy {
    suspend fun getOrFetch(
        key: String,
        maxAgeMinutes: Int = 1440,  // 24 hours
        fetcher: suspend () -> CurrencyRatesResponse
    ): CurrencyRatesResponse
}

// Example implementation
class CurrencyRatesRepository(
    private val api: CurrencyRatesApi,
    private val cache: LocalCache
) {
    suspend fun getLatestCurrencyRates(base: String = "EUR"): CurrencyRatesResponse {
        val cacheKey = "rates_$base"

        // Try cache first
        val cached = cache.get(cacheKey)
        if (cached != null && !isCacheExpired(cached)) {
            return cached
        }

        // Fetch from API if cache miss or expired
        return api.getLatestWithBase(base).also { response ->
            cache.put(cacheKey, response)
        }
    }

    private fun isCacheExpired(cached: CurrencyRatesResponse): Boolean {
        // Check if older than 24 hours
        return System.currentTimeMillis() - cached.timestamp > 24 * 60 * 60 * 1000
    }
}
```

---

## Integration Examples

### Complete Flow Example

```kotlin
// 1. API Interface (restapi module)
interface CurrencyRatesApi {
    @GET("latest")
    suspend fun getLatestWithBase(@Query("base") base: String): CurrencyRatesResponse
}

// 2. Repository (repository module)
class CurrencyRatesRepository(private val api: CurrencyRatesApi) {
    suspend fun getLatestCurrencyRates(): CurrencyRatesResponse {
        return api.getLatestWithBase("EUR")
    }
}

// 3. Use Case (domain module)
class GetLatestRatesUsecase(private val repository: CurrencyRatesRepository) {
    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        val response = repository.getLatestCurrencyRates()
        return response.rates?.toList() ?: emptyList()
    }
}

// 4. Presenter (app module)
class CurrencyPresenter(private val usecase: GetLatestRatesUsecase)
    : BasePresenter<CurrencyContract.View>() {

    override fun onButtonClickedEvent() {
        presenterScope.launch {
            try {
                val data = usecase.getLatestCurrencyRates()
                view?.onGetDataSuccess(data)
            } catch (e: Exception) {
                view?.onGetDataFailed(e)
            }
        }
    }
}

// 5. Screen (app module)
@Composable
fun CurrencyScreen(presenter: CurrencyPresenter = koinInject()) {
    var rates by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }

    val view = object : CurrencyContract.View {
        override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
            rates = data
        }
        // ... other methods
    }

    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.onButtonClickedEvent()
    }

    CurrencyList(rates)
}
```

---

## Testing API

### Unit Testing Repository

```kotlin
@RunWith(AndroidJUnit4::class)
class CurrencyRatesRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockApi: CurrencyRatesApi
    private lateinit var repository: CurrencyRatesRepository

    @Before
    fun setup() {
        mockApi = mockk()
        repository = CurrencyRatesRepository(mockApi)
    }

    @Test
    fun `getLatestCurrencyRates returns rates from API`() = runTest {
        // Arrange
        val mockResponse = CurrencyRatesResponse(
            base = "EUR",
            date = "2024-01-11",
            rates = mapOf("USD" to 1.0892, "GBP" to 0.8736)
        )
        coEvery { mockApi.getLatestWithBase("EUR") } returns mockResponse

        // Act
        val result = repository.getLatestCurrencyRates()

        // Assert
        assertEquals("EUR", result.base)
        assertEquals(2, result.rates?.size)
        coVerify { mockApi.getLatestWithBase("EUR") }
    }

    @Test
    fun `getLatestCurrencyRates throws exception on API error`() = runTest {
        // Arrange
        coEvery { mockApi.getLatestWithBase("EUR") } throws IOException("Network error")

        // Act & Assert
        assertThrows<IOException> {
            runTest { repository.getLatestCurrencyRates() }
        }
    }
}
```

### Integration Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class CurrencyScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        // Start Koin for DI
        startKoin {
            modules(testModules)
        }
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun currencyScreenDisplaysRates() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreen()
            }
        }

        // Verify loading state appears
        composeTestRule.onNodeWithText("Loading...")
            .assertIsDisplayed()

        // Wait for data load
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithContentDescription("Currency Item")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify data displayed
        composeTestRule.onNodeWithText("USD")
            .assertIsDisplayed()
    }
}
```

---

## Resources

- **API Documentation:** https://fixer.io/documentation
- **Supported Currencies:** https://fixer.io/currencies
- **REST Best Practices:** https://developer.mozilla.org/en-US/docs/Glossary/REST
- **Retrofit Documentation:** https://square.github.io/retrofit/
- **Kotlin Coroutines:** https://kotlinlang.org/docs/coroutines-overview.html

---

**Last Updated:** 2026-01-11
**Status:** Complete
**Ready for Use:** ✅ YES
