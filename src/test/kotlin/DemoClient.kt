import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable
import me.profiluefter.supabase.SupabaseClient

fun main() {
    val client = SupabaseClient(System.getenv("BASE_URL"), System.getenv("TOKEN"))

    client.subscribe<Vehicle>("public", "vehicles")
        .onInsert<Vehicle> {
            println("INSERT $it")
        }
        .onUpdate<Vehicle> {
            println("UPDATE $it")
        }
        .onDelete<Vehicle> {
            println("DELETE $it")
        }
        .join()

    val vehicles = client.from<Vehicle>("vehicles")
        .select()
        .executeAndGetList<Vehicle>()

    val inserted = client.from<Vehicle>("vehicles")
        .insert(Vehicle(name = "Demo Vehicle"))
        .executeAndGetList<Vehicle>()
        .single()

    client.from<Vehicle>("vehicles")
        .update(Vehicle(name = "Demo 2"))
        .eq("id", inserted.id!!)
        .execute()

    client.from<Vehicle>("vehicles")
        .delete()
        .eq("id", inserted.id)
        .execute()

    println(vehicles)
}

@Serializable
data class Vehicle(
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val id: Int? = null,
    val name: String = ""
)