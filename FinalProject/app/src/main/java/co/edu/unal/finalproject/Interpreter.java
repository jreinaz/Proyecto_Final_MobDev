package co.edu.unal.finalproject;

import android.util.Log;

import java.util.HashMap;

public class Interpreter<T> extends matlabBaseVisitor<T>{
    HashMap<String, Object> simTable = new HashMap<String, Object>();
    @Override
    public T visitPrimary_expression(matlabParser.Primary_expressionContext ctx) {
        //Log.i("primary_expression: ",ctx.getText());
        if(ctx.IDENTIFIER() != null){
            if(ctx.IDENTIFIER().getText() == "true"){
                Boolean result = ctx.IDENTIFIER().getText() =="true";
                return (T) result;
            }else if(ctx.IDENTIFIER().getText() == "false"){
                Boolean result = !(ctx.IDENTIFIER().getText() == "false");
                return (T) result;
            }
        }else if(ctx.CONSTANT() != null){
            Double num = new Double(ctx.CONSTANT().getText());
            return (T) num;
        }else if(ctx.expression() != null){
            return this.visitExpression(ctx.expression());
        }
        return null;
    }
}
