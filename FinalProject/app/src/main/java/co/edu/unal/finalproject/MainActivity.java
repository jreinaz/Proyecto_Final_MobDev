package co.edu.unal.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> series;
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

        double x_axis = 0.0;
        double y_axis;
        int numDataPoints = 100;

        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        for(int i = 0; i < numDataPoints; i++) {
            x_axis += 0.1;
            y_axis = Math.sin(x_axis);
            series.appendData(new DataPoint(x_axis, y_axis), true, 100);
        }
        graph.addSeries(series);
    }
}