#!/usr/bin/env groovy


def printDoubleLine() {
	println("=======================================");
}

def printSingleLine() {
	println("-------------------------------------------");
}

def info(message) {
	echo "INFO: " + message
}

def debug(message) {
    //echo "DEBUG: " + message
}

def warn(message) {
    println("WARN: " + message);
}

def error(message) {
	println("ERROR: " + message);
}
