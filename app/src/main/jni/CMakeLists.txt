cmake_minimum_required(VERSION 2.8.4)
project(vsa_demo_jni)

include_directories("./")

file(GLOB sources . *.cpp *.h
        include/*.h
        src/*.cpp
)

set(SOURCE_FILES ${sources} include/copytype.h)
add_library(vsa_demo_jni ${SOURCE_FILES})
