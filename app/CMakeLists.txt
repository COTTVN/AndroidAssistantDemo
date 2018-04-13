cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             ndkTest
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
             src/main/jni/ndkTest.c )

find_library( log-lib
              log )

target_link_libraries( ndkTest
                       ${log-lib} )