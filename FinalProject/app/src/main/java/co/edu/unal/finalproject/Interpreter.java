package co.edu.unal.finalproject;

import android.util.Log;

import java.util.HashMap;

public class Interpreter<T> extends matlabBaseVisitor<T>{
    HashMap<String, Object> simTable = new HashMap<String, Object>();

    @Override public T visitIteration_statement(matlabParser.Iteration_statementContext ctx) {
        if(ctx.WHILE() != null){
            while(this.visitExpression(ctx.expression()).equals(true)){
                this.visitStatement_list(ctx.statement_list());
            }
        }
        return null;
    }

    @Override public T visitSelection_statement(matlabParser.Selection_statementContext ctx) {
        if(ctx.IF() != null && ctx.ELSE() != null && ctx.elseif_clause() == null){
            if(this.visitExpression(ctx.expression()).equals(true)){
                this.visitStatement_list(ctx.statement_list(0));
            }else{
                this.visitStatement_list(ctx.statement_list(1));
            }
        }
        return null;
    }

    @Override public T visitAssignment_expression(matlabParser.Assignment_expressionContext ctx) {
        if(ctx.postfix_expression() != null && ctx.expression() != null){
            // check if symbol not in table
            if (!this.simTable.containsKey(ctx.postfix_expression().primary_expression().IDENTIFIER().getText())) {
                //console.log('first time '+ctx.postfix_expression().primary_expression().IDENTIFIER().getText());
                this.simTable.put(ctx.postfix_expression().primary_expression().IDENTIFIER().getText(),this.visitExpression(ctx.expression()));
                //console.log(this.simbTable);
            } else {
                this.simTable.put(ctx.postfix_expression().primary_expression().IDENTIFIER().getText(),this.visitExpression(ctx.expression()));
            }
        }
        return null;
    }


    @Override public T visitExpression(matlabParser.ExpressionContext ctx) {
        T ans = null;
        if(ctx.expression() == null){
            ans = this.visitOr_expression(ctx.or_expression());
        }
        return ans;
    }


    @Override public T visitOr_expression(matlabParser.Or_expressionContext ctx) {
        T ans = null;
        if(ctx.or_expression() == null){
            ans = this.visitAnd_expression(ctx.and_expression());
        }else{
            if(ctx.OR_OP() != null){
                Boolean value = new Boolean((boolean)this.visitOr_expression(ctx.or_expression()) || (boolean)this.visitAnd_expression(ctx.and_expression()));
                ans = (T) value;
            }
        }
        return ans;
    }


    @Override public T visitAnd_expression(matlabParser.And_expressionContext ctx) {
        T ans = null;
        if(ctx.and_expression() == null){
            ans = this.visitEquality_expression(ctx.equality_expression());
        }else{
            if(ctx.AND_OP() != null){
                Boolean value = new Boolean((boolean) this.visitAnd_expression(ctx.and_expression()) && (boolean) this.visitEquality_expression(ctx.equality_expression()));
                ans = (T) value;
            }
        }
        return ans;
    }

    @Override public T visitEquality_expression(matlabParser.Equality_expressionContext ctx) {
        T ans = null;
        if(ctx.equality_expression() == null){
            ans = this.visitRelational_expression(ctx.relational_expression());
        }else{
            if(ctx.EQ_OP() != null){
                Boolean value = new Boolean(this.visitEquality_expression(ctx.equality_expression()).equals(this.visitRelational_expression(ctx.relational_expression())));
                ans = (T) value;
            }else if(ctx.NE_OP() != null){
                Boolean value = new Boolean(!this.visitEquality_expression(ctx.equality_expression()).equals(this.visitRelational_expression(ctx.relational_expression())));
                ans = (T) value;
            }
        }
        return ans;
    }


    @Override public T visitRelational_expression(matlabParser.Relational_expressionContext ctx) {
        T ans = null;
        if(ctx.relational_expression() == null){
            ans = this.visitAdditive_expression(ctx.additive_expression());
        }else{
            if(ctx.L_OP() != null){
                Boolean value = new Boolean((double)this.visitRelational_expression(ctx.relational_expression()) < (double)this.visitAdditive_expression(ctx.additive_expression()));
                ans = (T) value;
            }else if(ctx.G_OP() != null){
                Boolean value = new Boolean((double) this.visitRelational_expression(ctx.relational_expression()) > (double) this.visitAdditive_expression(ctx.additive_expression()));
                ans = (T) value;
            }else if(ctx.LE_OP() != null){
                Boolean value = new Boolean((double) this.visitRelational_expression(ctx.relational_expression()) <= (double) this.visitAdditive_expression(ctx.additive_expression()));
                ans = (T) value;
            }else if(ctx.GE_OP() != null){
                Boolean value = new Boolean((double) this.visitRelational_expression(ctx.relational_expression()) >= (double) this.visitAdditive_expression(ctx.additive_expression()));
                ans = (T) value;
            }
        }
        return ans;
    }

    @Override public T visitAdditive_expression(matlabParser.Additive_expressionContext ctx) {
        T ans = null;
        if(ctx.additive_expression() == null){
            ans = this.visitMultiplicative_expression(ctx.multiplicative_expression());
        }else{
            if(ctx.ADD_OP() != null){
                Double value = new Double((double) this.visitAdditive_expression(ctx.additive_expression()) + (double) this.visitMultiplicative_expression(ctx.multiplicative_expression()));
                ans = (T) value;
            }else if(ctx.SUB_OP() != null){
                Double value = new Double((double) this.visitAdditive_expression(ctx.additive_expression()) - (double) this.visitMultiplicative_expression(ctx.multiplicative_expression()));
                ans = (T) value;
            }
        }
        return ans;
    }


    @Override public T visitMultiplicative_expression(matlabParser.Multiplicative_expressionContext ctx) {
        T ans = null;
        if(ctx.multiplicative_expression() == null){
            ans = this.visitUnary_expression(ctx.unary_expression());
        }else{
            if(ctx.MUL_OP() != null){
                Double value = new Double((double) this.visitMultiplicative_expression(ctx.multiplicative_expression()) * (double) this.visitUnary_expression(ctx.unary_expression()));
                ans = (T) value;
            }else if(ctx.DIV_OP() != null){
                Double value = new Double((double) this.visitMultiplicative_expression(ctx.multiplicative_expression()) / (double) this.visitUnary_expression(ctx.unary_expression()));
                ans = (T) value;
            }else if(ctx.POW_OP() != null){
                Double value = new Double(Math.pow((double) this.visitMultiplicative_expression(ctx.multiplicative_expression()),(double) this.visitUnary_expression(ctx.unary_expression())));
                ans = (T) value;
            }
        }
        return ans;
    }


    @Override public T visitUnary_expression(matlabParser.Unary_expressionContext ctx) {
        T ans = null;
        if(ctx.unary_operator() == null){
            ans = this.visitPostfix_expression(ctx.postfix_expression());
        }else{
            if(ctx.unary_operator().ADD_OP() != null){
                //console.log('visit unary expression: '+this.visitPostfix_expression(ctx.postfix_expression()));
                ans = this.visitPostfix_expression(ctx.postfix_expression());
            }else if(ctx.unary_operator().SUB_OP() != null){
                Double value = new Double((double) this.visitPostfix_expression(ctx.postfix_expression()) * -1.0);
                ans = (T) value;
            }else if(ctx.unary_operator().NEG_OP() != null){
                Boolean value = !((boolean) this.visitPostfix_expression(ctx.postfix_expression()));
                ans = (T) value;
            }
        }
        return ans;
    }

    @Override public T visitPostfix_expression(matlabParser.Postfix_expressionContext ctx) {
        T ans = null;
        if(ctx.primary_expression() != null){
            ans = this.visitPrimary_expression(ctx.primary_expression());
        }
        return ans;
    }


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
            if(this.simTable.get(ctx.IDENTIFIER().getText()) != null){
                if(this.simTable.get(ctx.IDENTIFIER().getText()).toString() == "true"){
                    Boolean result = this.simTable.get(ctx.IDENTIFIER().getText()).toString() == "true";
                    return (T) result;
                }else if(this.simTable.get(ctx.IDENTIFIER().getText()).toString() == "false"){
                    Boolean result = !(this.simTable.get(ctx.IDENTIFIER().getText()).toString() == "false");
                    return (T) result;
                }else{
                    Double num = new Double(this.simTable.get(ctx.IDENTIFIER().getText()).toString());
                    return (T) num;
                }
            }else{
                Log.i("Semantic Err, no var: ",ctx.IDENTIFIER().getText());
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
