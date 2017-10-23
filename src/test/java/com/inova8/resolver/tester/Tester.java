package com.inova8.resolver.tester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.inova8.resolver.*;

public class Tester
{
	public static void main(String[] args) throws IOException
	{
		ResolverClass Resolver = new ResolverClass();
		String CellReference = "";
		String FormulaReference = "";
		String Equality = "";
		String CellConstraintReference = "";
		String FormulaConstraintReference = "";
		String Measurement = "";
		String variable = "";
		String initial;
		String measurement;
		String tolerance;
		String name = "";
		String address = "";
		String[] sarr;
		String grdResult = "";

		String test = "SimpleLinear";

		File fn = new File( test + ".names.txt");
		File fi = new File( test + ".constraints.txt");
		File fe = new File( test + ".expressions.txt");
		File fm = new File( test + ".measurements.txt");
		File fr = new File( test + ".results.xml");
		File fl = new File("log.txt");
		BufferedWriter  wr =  new BufferedWriter(new FileWriter(fr));//fr.CreateText();
		BufferedWriter  wl =  new BufferedWriter(new FileWriter(fl));//fl.CreateText();

		BufferedReader n = new BufferedReader(new FileReader(fn));//fn.OpenText();

		Resolver = new ResolverClass();
		String Constraint;
		while ((Constraint = n.readLine()) != null)
		{
			//String Constraint = n.readLine();
			sarr = Constraint.split("[;]", -1);
			if (sarr.length == 2)
			{
				name = sarr[0].trim();
				address = sarr[1].trim();

				if (!(Resolver.addName(name,address)))
				{
					wl.write(name + address+"\n");
				}
			}
		}

		n.close();

		BufferedReader r = new BufferedReader(new FileReader(fi));//fi.OpenText();


		while ((Constraint = r.readLine()) != null)
		{
			//String Constraint = r.ReadLine();
			sarr = Constraint.split("[;]", -1);
			if (sarr.length == 5)
			{
				CellReference = sarr[0].trim();
				FormulaReference = sarr[1].trim();
				Equality = sarr[2].trim();
				CellConstraintReference = sarr[3].trim();
				FormulaConstraintReference = sarr[4].trim();

				if (!(Resolver.addConstraint(CellReference, FormulaReference, Equality, CellConstraintReference, FormulaConstraintReference)))
				{
					wl.write(FormulaReference + Equality + FormulaConstraintReference+"\n");
				}
			}
		}

		r.close();

		BufferedReader e = new BufferedReader(new FileReader(fe));//fe.OpenText();
		String Expression;
		while ((Expression = e.readLine()) != null)
		{
			//String Expression = e.ReadLine();
			sarr = Expression.split("[;]", -1);
			if (sarr.length == 2)
			{
				String label = sarr[0].trim();
				String expression = sarr[1].trim();

				if (!(Resolver.addDependent(label, expression)))
				{
				}
			}
		}
		e.close();

		BufferedReader m = new BufferedReader(new FileReader(fm));//fm.OpenText();

		while ((Measurement = m.readLine()) != null)
		{
			//Measurement = m.ReadLine();
			sarr = Measurement.split("[;]", -1);
			if (sarr.length >= 4)
			{
				variable = sarr[0].trim();
				initial = sarr[1].trim();
				measurement = sarr[2].trim();
				tolerance = sarr[3].trim();
				//NonNegative = sarr[4];
				//if (!(Resolver.addVariable(variable, "", measurement, tolerance, true)))
				if (!(Resolver.addVariable(variable, initial, measurement, tolerance, true)))
				{
				}
			}
		}
		m.close();
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);

		grdResult = "<RECONCILIATION ReconciledCost='" + Resolver.getReconciledCost() + "'";
		grdResult += " GlobalCriticalValue='" + Resolver.getGlobalCriticalValue() + "'";
		grdResult += " RedundancyDegree='" + Resolver.getRedundancyDegree() + "'";
		grdResult += ">";
		wr.write(grdResult+"\n");

		grdResult = "<CONVERGENCE Converged='" + Resolver.getConvergence().getConverged() + "'" + "\r\n";
		grdResult += (" Termination='" + Resolver.getConvergence().getTerminated() + "'") + "\r\n";
		grdResult += (" ConvergenceTime(ms)='" + Resolver.getConvergence().getConvergenceTime() + "'") + "\r\n";
		grdResult += (" Iterations='" + Resolver.getConvergence().getIterations() + "'") + "\r\n";
		grdResult += (" CalculatedPrecision='" + Resolver.getConvergence().getCalculatedPrecision() + "'") + "\r\n";
		grdResult += (" CalculatedConvergence='" + Resolver.getConvergence().getCostChange() + "'") + "\r\n";
		//grdResult += (" PreviousCalculatedPrecision='" + Resolver.Convergence.PreviousCalculatedPrecision + "'") + "\r\n";
		//grdResult += (" Cost='" + Resolver.Convergence.Cost + "'") + "\r\n";
		//grdResult += (" PreviousCost='" + Resolver.Convergence.PreviousCost + "'") + "\r\n";
		wr.write(grdResult+"\n");
		grdResult = ("<LOG" + "\r\n'" + Resolver.getConvergence().getLog() + "'/>");
		grdResult += "</CONVERGENCE>";

		wr.write(grdResult+"\n");

		grdResult = "<CONSTRAINTS ConstraintCriticalValue='" + Resolver.getConstraintCriticalValue() + "'";
		grdResult += " Total='" + (new Integer(Resolver.getConstraints().getCount())).toString() + "'";
		grdResult += " Linear='" + (new Integer(Resolver.getConstraints().getLinearConstraints())).toString() + "'";
		grdResult += " Nonlinear='" + (new Integer(Resolver.getConstraints().getNonlinearConstraints())).toString() + "'";
		grdResult += " ActiveLinearInequality='" + Resolver.getConstraints().getActiveLinearInequalityConstraints() + "'";
		grdResult += " ActiveNonlinearInequality='" + Resolver.getConstraints().getActiveNonlinearInequalityConstraints() + "'";
		grdResult += ">";
		wr.write(grdResult+"\n");

		for (ConstraintClass constraint : Resolver.getConstraints())
		{
			grdResult = "<CONSTRAINT Constraint='" + constraint.getAddress() + "'";
			grdResult += (" Active='" + constraint.getActive() + "'");
			grdResult += (" ReconciledResidual='" + constraint.getReconciledResidual() + "'");
			grdResult += (" MeasuredResidual='" + constraint.getMeasuredResidual() + "'");
			grdResult += (" ReconciledDeviation='" + constraint.getReconciledDeviation() + "'");
			grdResult += (" MeasuredTest='" + constraint.getMeasuredTest() + "'");
			grdResult += (" ConstraintTest='" + constraint.getReconciledTest() + "'>");
			grdResult += "<SERIALIZE Constraint='" + constraint.getSerialize() + "'/>";
			grdResult += "<TRACE Constraint='" + constraint.getTrace() + "'/>";
			grdResult += "</CONSTRAINT>";

			wr.write(grdResult+"\n");

		}
		grdResult = "</CONSTRAINTS>";
		wr.write(grdResult+"\n");
		grdResult = "<DEPENDENTS NumberDependents='" + (new Integer(Resolver.getDependents().getCount())).toString() + "'>";
		wr.write(grdResult+"\n");

		for (DependentClass dependent : Resolver.getDependents())
		{
			grdResult = "<DEPENDENT Cell='" + dependent.getAddress() + "'";
			grdResult += (" Evaluation='" + dependent.getEvaluation() + "'>");
			grdResult += "<SERIALIZE Dependent='" + dependent.getSerialize() + "'/>";
			grdResult += "<TRACE Dependent='" + dependent.getTrace() + "'/>";
			grdResult += "</DEPENDENT>";

			wr.write(grdResult+"\n");
		}
		grdResult = "</DEPENDENTS>";
		wr.write(grdResult+"\n");

		grdResult = "<RESULTS MeasurementCriticalValue='" + Resolver.getMeasurementCriticalValue() + "'";
		grdResult += " NumberResults='" + (new Integer(Resolver.getResults().getCount())).toString() + "'";
		grdResult += " FixedVariables='" + Resolver.getResults().getFixedValues() + "'";
		grdResult += ">";
		wr.write(grdResult+"\n");

		for (ResultClass result : Resolver.getResults())
		{
			grdResult = "<RESULT Cell='" + result.getCellName() + "'";
			grdResult += (" ReconciledValue='" + result.getReconciledValue() + "'");
//C# TO JAVA CONVERTER TODO TASK: Arithmetic operations involving nullable type instances are not converted to null-value logic:
			grdResult += (" MeasuredValue='" + result.getMeasuredValue() + "'");
			grdResult += (" Solvability='" + result.getSolvabilityText() + "'");
			grdResult += (" MeasuredTest='" + result.getMeasuredTest() + "'");
			grdResult += (" ReconciledTest='" + result.getReconciledTest() + "'");
//C# TO JAVA CONVERTER TODO TASK: Arithmetic operations involving nullable type instances are not converted to null-value logic:
			grdResult += (" MeasuredTolerance='" + result.getMeasuredTolerance() + "'");
			grdResult += (" ReconciledTolerance='" + result.getReconciledTolerance() + "'");
			grdResult += ("/>");

			wr.write(grdResult+"\n");
		}
		grdResult = "</RESULTS>";
		wr.write(grdResult+"\n");
		grdResult = "</RECONCILIATION>";
		wr.write(grdResult+"\n");

		wr.close();
		wl.close();
	}
}