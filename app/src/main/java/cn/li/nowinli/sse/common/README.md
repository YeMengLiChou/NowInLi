# Ktor 后端代码

`build.gradle.kts` 文件:
```kotlin
val ktorVersion: String by project // 3.0.0-beta-1
val kotlinVersion: String by project

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("io.ktor:ktor-server-sse:$ktorVersion")
    implementation("com.h2database:h2:2.1.214")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

```



```kotlin
fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        watchPaths = listOf("classes"),
        module = Application::module
    ).start(true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        json()
    }

}

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost()
    }
}

fun Application.module() {
    install(SSE)
    configureHTTP()
    configureSerialization()
    routes()
}

val states = MutableSharedFlow<String>()

fun Application.routes() {
    routing {
        sse("/events") {
            send(
                ServerSentEvent(
                    data = "connected",
                )
            )
            states.collectLatest { value ->
                log.info("receive: $value")
                try {
                    send(
                        ServerSentEvent(
                            data = value,
                            event = "normal",
                            id = System.currentTimeMillis().toString()
                        )
                    )
                } catch (e: Exception) {
                    log.error(e)
                    this@sse.close()
                    this@sse.cancel()
                }
            }
        }
        post("/send") {
            val value = call.receive<String>()
            states.emit(value)
            call.respond(HttpStatusCode.OK, ApiResult(200, "sended", null))
        }
    }
}
```

