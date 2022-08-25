import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.profiluefter.supabase.SupabaseClient
import kotlin.random.Random
import kotlin.random.nextUInt

suspend fun main() {
    val client = SupabaseClient(
        "http://localhost:64321",
        // DEFAULT anon token (not a leaked secret)
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6ImFub24ifQ.625_WdcF3KHqz5amU0x2X5WWHP-OEs_4qj0ssLNHzTs"
    )

    val randomUser = Random.nextUInt()

    client.signUp("$randomUser@example.org", "123456")
    client.signIn("$randomUser@example.org", "123456")

    println(client.user)

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
        .join(::println, ::println) {
            println("Joined realtime")
        }

    val vehicles = client.from<Vehicle>("vehicles")
        .select()
        .executeAndGetList<Vehicle>()

    println(vehicles)

    val inserted = client.from<Vehicle>("vehicles")
        .insert(Vehicle(name = "Demo Vehicle"))
        .executeAndGetList<Vehicle>()
        .single()

    client.from<Vehicle>("vehicles")
        .update(Vehicle(name = "Demo 2"))
        .eq("id", inserted.id!!)
        .execute()

//    client.from<Vehicle>("vehicles")
//        .delete()
//        .eq("id", inserted.id)
//        .execute()

    val updatedVehicles = client.from<Vehicle>("vehicles")
        .select()
        .executeAndGetList<Vehicle>()

    println(updatedVehicles)

    client.disconnect {
        println("Disconnected realtime")
    }
    client.signOut()
}

@Serializable
data class Vehicle(
    val id: Int? = null,
    @SerialName("user_id")
    val userId: String? = null,
    val name: String = ""
)