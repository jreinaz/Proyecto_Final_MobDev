package co.edu.unal.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private matlabLexer lexer = new matlabLexer(CharStreams.fromString("2.0;"));
    private CommonTokenStream tokens = new CommonTokenStream(lexer);
    private matlabParser parser = new matlabParser(tokens);
    private ParseTree tree = parser.translation_unit();
    private Interpreter interpreter = new Interpreter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interpreter.visit(tree);
    }
}