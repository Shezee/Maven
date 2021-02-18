package com.sample.maven;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.FileNotFoundException;


public class customlisteners implements ITestListener {

    public customlisteners() throws FileNotFoundException {
    }

    @Override
    public void onFinish(ITestContext arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart(ITestContext arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailure(ITestResult arg0) {
        System.out.println("This is on testFailure");
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {
        System.out.println("This is skip exception");

    }

    @Override
    public void onTestStart(ITestResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestSuccess(ITestResult arg0) {
        // TODO Auto-generated method stub

    }

}
