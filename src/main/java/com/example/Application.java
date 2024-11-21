package com.example;

import com.webforj.App;
import com.webforj.annotation.AppTitle;
import com.webforj.annotation.Routify;
import com.webforj.annotation.StyleSheet;

/**
 * A simple HelloWorld app.
 */
@Routify(packages = "com.example.views")
@AppTitle("MyTime - Time management application")
@StyleSheet("ws://applayout.css")
public class Application extends App{
}
