plugins{java}
dependencies{compileOnly(files("C:/Users/TOWUK/Documents/Mindustry/core/build/libs/core-release-deps.jar"))}
tasks.compileJava{options.compilerArgs.add("-g:none")}
