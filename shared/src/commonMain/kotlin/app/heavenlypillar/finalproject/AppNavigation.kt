package app.heavenlypillar.finalproject

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import app.heavenlypillar.finalproject.Destinations.Home
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

sealed interface Destinations : NavKey {
    @Serializable
    object Home: Destinations
    @Serializable
    object SingIn: Destinations
}

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destinations.Home::class, Destinations.SingIn.serializer())
            subclass(Destinations.SingIn::class, Destinations.Home.serializer())
        }
    }
}

@Composable
fun AppNavigation(){
    val backStack = rememberNavBackStack()
    navDisplay()
}