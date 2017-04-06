# Minesweeper
v.0.1

Export this repository to your IDE. In our case it is IntelliJ IDEA.
Then you should add libraries in your project.

Go File -> Project structure -> Libraries -> Add new project library (at the top left side) -> Choose lwjgl-2.9.3 folder in lwjgl-libs folder.

Then go File -> Project structure -> SDKs -> Add (at the right side) -> add everything from lwjgl-2.9.3\jar folder.

And the final step - go Run -> Edit configurations -> Find VM options -> insert here  "-Djava.library.path=lwjgl_libs/"

And here we done.
