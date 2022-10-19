package co.edu.unal.finalproject;

import android.util.Log;

public class Interpreter<T> extends matlabBaseVisitor<T>{
    @Override
    public T visitPrimary_expression(matlabParser.Primary_expressionContext ctx) {
        Log.i("primary_expression: ",ctx.getText());
        return super.visitPrimary_expression(ctx);
    }
}
