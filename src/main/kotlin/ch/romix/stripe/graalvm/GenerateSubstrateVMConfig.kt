package ch.romix.stripe.graalvm

import com.google.common.reflect.ClassPath
import com.google.gson.GsonBuilder
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main() {
    val content = getReflectConfigContent()
    writeReflectConfigFile(content)
}

private val PACKAGES = listOf("recurly", "stripe")

@Throws(URISyntaxException::class, ClassNotFoundException::class)
private fun getReflectConfigContent(): String {
    val allClassDescriptions = getAllClasses()
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(allClassDescriptions)
}

@Throws(IOException::class)
private fun writeReflectConfigFile(serialized: String) {
    val reflectConfigPath = Paths
        .get("./src/main/resources/META-INF/native-image/ch.romix/stripe-java/reflect-config.json")
    val file = reflectConfigPath.toFile()
    file.parentFile.mkdirs()
    file.createNewFile()
    Files.newBufferedWriter(reflectConfigPath).use { writer -> writer.write(serialized) }
}

@Throws(ClassNotFoundException::class)
private fun getAllClasses(): ArrayList<ReflectionClassDescription> {
    val cp: ClassPath = ClassPath.from(Thread.currentThread().contextClassLoader)
    val classes = ArrayList<ReflectionClassDescription>()
    for (info in cp.allClasses) {
        if (PACKAGES.any { info.name.contains(it) }) {
            classes.add(ReflectionClassDescription(info.name))
        }
    }
    return classes
}
