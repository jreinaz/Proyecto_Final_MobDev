package co.edu.unal.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matlabLexer lexer = null;
        try {
            lexer = new matlabLexer(CharStreams.fromStream(getResources().openRawResource(R.raw.input)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        matlabParser parser = new matlabParser(tokens);
        ParseTree tree = parser.translation_unit();
        Interpreter interpreter = new Interpreter();
        interpreter.visit(tree);
    }
}