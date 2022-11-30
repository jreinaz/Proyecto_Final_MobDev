package co.edu.unal.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer movePlayer;
    private LineGraphSeries<DataPoint> series;
    private PointsGraphSeries<DataPoint> pointSeries;

    private EditText initialPointEdit;
    private EditText paddingEdit;
    private EditText epsilonEdit;
    private EditText learningRateEdit;
    private EditText functionEdit;
    private EditText algoTypeEdit;
    private Button startButton;
    private Button nextStepButton;
    private String gradientDescentAlg =
            "while( ((alpha*(((f(xi + epsilon) - f(xi - epsilon))/(2*epsilon))))^2)^0.5 > epsilon)\n"
            +"  xi = xi - (alpha * ((f(xi + epsilon) - f(xi - epsilon))/(2*epsilon)));\n"
            +"end;";
    private String newtonRaphsonAlg =
            "xf = xi - (f(xi) / ((f(xi + epsilon) - f(xi - epsilon))/(2*epsilon)));\n" +
            "while(((xf-xi) ^ 2)^0.5 > epsilon)\n" +
            "   xi = xf;\n" +
            "   xf = xi - (f(xi) / ((f(xi + epsilon) - f(xi - epsilon))/(2*epsilon)));\n" +
            "end;";
    private GraphView graph;
    private ArrayList<Double> x_variation;
    private Function<Double,Double> function;
    private Interpreter interpreter;
    private int index;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialPointEdit = (EditText) findViewById(R.id.numXi);
        paddingEdit = (EditText) findViewById(R.id.paddingVal);
        epsilonEdit = (EditText) findViewById(R.id.epsilonVal);
        learningRateEdit = (EditText) findViewById(R.id.learning_rate_Input);
        functionEdit = (EditText) findViewById(R.id.functionInput);
        algoTypeEdit = (EditText) findViewById(R.id.algorithm_type_input);
        graph = (GraphView) findViewById(R.id.graph);
        x_variation = new ArrayList<Double>();
        interpreter = new Interpreter(x_variation);
        index = 0;
        series = new LineGraphSeries<>();
        pointSeries = new PointsGraphSeries<>();
        startButton = (Button) findViewById(R.id.btnStart);
        startButton.setEnabled(true);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String algorithm = "";
                if(algoTypeEdit.getText().toString().equals("1")){
                    String input = "xi = "+initialPointEdit.getText().toString()+";\n" +
                                    "epsilon = "+epsilonEdit.getText().toString()+";\n" +
                                    "alpha = "+learningRateEdit.getText().toString()+";\n" +
                                    "f = @(x) "+functionEdit.getText().toString()+";\n";
                    algorithm = input + gradientDescentAlg;
                }else if(algoTypeEdit.getText().toString().equals("2")){
                    String input = "xi = "+initialPointEdit.getText().toString()+";\n" +
                            "epsilon = "+epsilonEdit.getText().toString()+";\n" +
                            "f = @(x) "+functionEdit.getText().toString()+";\n";
                    algorithm = input + newtonRaphsonAlg;
                }
                matlabLexer lexer = new matlabLexer(CharStreams.fromString(algorithm));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                matlabParser parser = new matlabParser(tokens);
                ParseTree tree = parser.translation_unit();
                interpreter.visit(tree);
                double x_axis = Double.parseDouble(initialPointEdit.getText().toString()) - Double.parseDouble(paddingEdit.getText().toString());
                double y_axis;
                int numDataPoints = (int) ((int) ((Double.parseDouble(initialPointEdit.getText().toString()) + Double.parseDouble(paddingEdit.getText().toString()))-x_axis)/0.1);
                function = (Function<Double, Double>) interpreter.simTable.get("f");
                for(int i = 0; i < numDataPoints; i++) {
                    x_axis += 0.1;
                    y_axis = function.apply(x_axis);
                    series.appendData(new DataPoint(x_axis, y_axis), true, 100);
                }
                graph.addSeries(series);
                double xval = (double) interpreter.x_variation.get(index);
                DataPoint point = new DataPoint(xval,function.apply(xval));
                pointSeries.appendData(point,true,1);
                pointSeries.setSize(10f);
                if(algoTypeEdit.getText().toString().equals("1")){
                    pointSeries.setColor(Color.GREEN);
                }else if(algoTypeEdit.getText().toString().equals("2")){
                    pointSeries.setColor(Color.RED);
                }
                graph.addSeries(pointSeries);
                movePlayer.start();
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setXAxisBoundsManual(true);
                index = index + 1;
            }
        });

        nextStepButton = (Button) findViewById(R.id.btnNextStep);
        nextStepButton.setEnabled(true);
        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index <= interpreter.x_variation.size()-1){
                    double xval = (double) interpreter.x_variation.get(index);
                    DataPoint point = new DataPoint(xval,function.apply(xval));
                    DataPoint [] points = {point};
                    pointSeries.resetData(points);
                    movePlayer.start();
                    index = index + 1;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        movePlayer = MediaPlayer.create(getApplicationContext(), R.raw.movesound);
    }

    @Override
    protected void onPause() {
        super.onPause();
        movePlayer.release();
    }
}