import com.fasterxml.jackson.annotation.JsonProperty
import me.profiluefter.supabase.SupabaseClient

fun main() {
    val client = SupabaseClient(System.getenv("BASE_URL"), System.getenv("TOKEN"))

    val vehicles = client.from<Vehicle>("vehicles")
        .select()
        .executeAndGetList<Vehicle>()

    println(vehicles)
}

data class Vehicle(
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val id: Int? = null,
    val name: String
)