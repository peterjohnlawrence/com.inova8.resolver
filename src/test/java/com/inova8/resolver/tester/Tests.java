package com.inova8.resolver.tester;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.inova8.resolver.ResolverClass;

public class Tests {


	@Test
	public void ammonia() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "Ammonia";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}
	@Test
	public void largeBilinear() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "LargeBilinear";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}
	@Test
	public void largeNL() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "LargeNL";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}	
	@Test
	public void simpleLinear() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "SimpleLinear";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}
	@Test
	public void simpleVolume() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "SimpleVolume";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}
	@Test
	public void veryNL() throws IOException {
		ResolverClass Resolver = new ResolverClass();
		String test = "src\\test\\resources\\"+ "VeryNL";		
		Resolver = IOUtils.ReadModel(Resolver, test);
		//Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
		Resolver.Resolve(1000, 100,.000001, 0.0001);
		IOUtils.WriteResults(Resolver, test);
	}	
}
