package com.inova8.resolver;

/*  The equations are organized as 'nodes' in a binary directed graph
 *  So for a user defined function with multiple arguments such as 'USERDEF(arg1,arg1,arg3)'
 *  the nodal structure would be as follows:
 *  
 *                                                  |                         
 *                       Node(operatorType:Function,functionType:USERDEF,operand:NULL)
 *                      /                                                              \                   
 *                     Left                                                             Right
 *                    /                                                                  \ 
 *    Node(operatorType:ptrVariable,functionType:USERDEF,operand:arg1)                  Node(operatorType:ListSeparator,functionType:USERDEF,operand:NULL)
 *   /                                                               \                  /                                                              \                   
 *  -                                                                 -                Left                                                             Right
 *                                                                                    /                                                                  \ 
 *                                                                       Node(operatorType:ptrVariable,functionType:USERDEF,operand:arg2)               Node(operatorType:ListSeparator,functionType:USERDEF,operand:NULL)
 *                                                                      /                                                                \              /                                                              \
 *                  -                                                  -                                                                  -            Left                                                             -
 *                                                                                                                                                    /    
 *                                                                                                                                     Node(operatorType:ptrVariable,functionType:USERDEF,operand:arg3)   
 *                                                                                                                                     /                                                            \
 *                                                                                                                                    -                                                              -
 * 
 *  Resolver needs to evaulate both the user defined function and the derivative of that function at current conditions with respect to a particular variable (wrt). 
 *  To perform some statistical analysis, Resolver also needs to evaluate the user defined function and the derivative of that function with respect to a particular variable (wrt) at initial conditions. 
 *  
 */
public class UserFunctions
{
	/*
	 *  SUM(arg1, arg2, arg3, ...) where argn can be a literal, variable, another expression, or a range
	 *  Evaluated recursively as follows:
	 *  evaluate = arg1 + evaluate(arg2,arg3, ...)
	 *  derivative = d.arg1 + derivative(arg2,arg3, ...)
	 */
	public final double SUM_Evaluate(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		double evaluate = thisNode.getLeft().Evaluate(functionContext);
		if (thisNode.getRight() != null)
		{
			evaluate += thisNode.getRight().Evaluate(functionContext);
		}
		return evaluate;
	}
	public final double SUM_EvaluateResidual(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		double evaluateResidual = thisNode.getLeft().EvaluateResidual(functionContext);
		if (thisNode.getRight() != null)
		{
			evaluateResidual += thisNode.getRight().EvaluateResidual(functionContext);
		}
		return evaluateResidual;
	}
	public final double SUM_Derivative(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		double derivative = thisNode.getLeft().Derivative(wrt, functionContext);
		if (thisNode.getRight() != null)
		{
			derivative += thisNode.getRight().Derivative(wrt, functionContext);
			;
		}
		return derivative;
	}
	public final double SUM_DerivativeResidual(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		double derivativeResidual = thisNode.getLeft().DerivativeResidual(wrt, functionContext);
		if (thisNode.getRight() != null)
		{
			derivativeResidual += thisNode.getRight().DerivativeResidual(wrt, functionContext);
		}
		return derivativeResidual;
	}
	/*
	 *  PRODUCT(arg1, arg2, arg3, ...)  where argn can be a literal, variable, another expression, or a range
	 *  Evaluated recursively as follows:
	 *  evaluate = arg1 * evaluate(arg2,arg3, ...)
	 *  derivative = arg1 * derivative(arg2,arg3, ...) +  d.arg1 * evaluate(arg2,arg3, ...) 
	 */
	public final double PRODUCT_Evaluate(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		double evaluate = thisNode.getLeft().Evaluate(functionContext);
		if (thisNode.getRight() != null)
		{
			evaluate *= thisNode.getRight().Evaluate(functionContext);
		}
		return evaluate;
	}
	public final double PRODUCT_EvaluateResidual(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		double evaluateResidual = thisNode.getLeft().EvaluateResidual(functionContext);
		if (thisNode.getRight() != null)
		{
			evaluateResidual *= thisNode.getRight().EvaluateResidual(functionContext);
		}
		return evaluateResidual;
	}
	public final double PRODUCT_Derivative(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		double leftDerivative = thisNode.getLeft().Derivative(wrt, functionContext);
		double derivative = 0.0;
		if (thisNode.getRight() != null)
		{
			derivative = (thisNode.getLeft().Evaluate(functionContext) * thisNode.getRight().Derivative(wrt, functionContext)) + (leftDerivative * thisNode.getRight().Evaluate(functionContext));
		}
		return derivative;
	}
	public final double PRODUCT_DerivativeResidual(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		double leftDerivativeResidual = thisNode.getLeft().Derivative(wrt, functionContext);
		double derivativeResidual = 0.0;
		if (thisNode.getRight() != null)
		{
			double rightDerivativeResidual = thisNode.getRight().DerivativeResidual(wrt, functionContext);
			derivativeResidual = (thisNode.getLeft().EvaluateResidual(functionContext) * rightDerivativeResidual) + (leftDerivativeResidual * thisNode.getRight().EvaluateResidual(functionContext));
		}
		return derivativeResidual;
	}
	/*
	 *  USERDEF(arg1, arg2, arg3, ...) where argn can be a literal, variable, another expression, or a range
	 *  Example shown is simply the identity I(arg1), any further arguments being ignored. 
	*/
	public final double USERDEF_Evaluate(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		//Evaluate,  the function evaluated  at the current values of the reconciled variables
		return thisNode.getLeft().Evaluate(functionContext);
	}
	public final double USERDEF_EvaluateResidual(NodeClass thisNode, Constants.FunctionType functionContext)
	{
		//EvaluateResidual, the function evaluated at the initial conditions of the problem which really means the initial measurements
		return thisNode.getLeft().EvaluateResidual(functionContext);
	}
	public final double USERDEF_Derivative(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		//Derivative(wrt) the derivative of the function with respect to one of the reconciled variables evaluated at the current values of the reconciled variables
		return thisNode.getLeft().Derivative(wrt, functionContext);
	}
	public final double USERDEF_DerivativeResidual(NodeClass thisNode, CellClass wrt, Constants.FunctionType functionContext)
	{
		//DerivativeResidual(wrt) the derivative function  with respect to one of the reconciled variables evaluated at the initial conditions of the problem which really means the initial measurements
		return thisNode.getLeft().DerivativeResidual(wrt, functionContext);
	}
}