package cn.li.convention

import cn.li.convention.config.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(target.pluginManager) {
                apply("com.google.dagger.hilt.android")
            }
            target.dependencies {
                add("implementation", libs.findLibrary("hilt.android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
            }

        }

    }
}