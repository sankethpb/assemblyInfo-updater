package com.etas.jenkins.plugins.VersionPatcher;

import static org.junit.Assert.assertTrue;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

public class VersionPatcherTest {

	@Rule public JenkinsRule j = new JenkinsRule();
	
	@Test
	public void testAssemblyInfoCsUpdate() throws InterruptedException, IOException, Exception {
		
		EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        envVars.put("PVERSION", "1.1.0.0");
        envVars.put("FVERSION", "2.2.0.5");
        envVars.put("COPYRIGHT", "MyCompany");
        j.jenkins.getGlobalNodeProperties().add(prop);
        FreeStyleProject project = j.createFreeStyleProject();
        
        project.getBuildersList().add(new TestBuilder() {
			
			@Override
			public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
					BuildListener listener) throws InterruptedException, IOException {
				
				build.getWorkspace().child("AssemblyInfo.cs").write("using System.Reflection;\n" +
						"\n" +
						"[assembly: AssemblyTitle(\"\")]\n" +
						"[assembly: AssemblyDescription(\"\")]\n" +
						"[assembly: AssemblyCompany(\"\")]\n" +
						"[assembly: AssemblyFileVersion(\"12.13.24.56\")]\n" +
						"[assembly: AssemblyCopyright(\"\")]\n" +
						"[assembly: AssemblyTrademark(\"\")]\n" +
						"[assembly: AssemblyCulture(\"\")]\n" +
						"[assembly: AssemblyVersion(\"12.1.14.96\")]", "UTF-8");
				
				return true;
				
			}
		});
        
        VersionPatcherBuilder builder = new VersionPatcherBuilder("${WORKSPACE}", "AssemblyInfo.cs", "${FVERSION}", "${PVERSION}", "${COPYRIGHT}");
        project.getBuildersList().add(builder);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        String content = build.getWorkspace().child("AssemblyInfo.cs").readToString();
        assertTrue(content.contains("AssemblyVersion(\"1.1.0.0"));
        assertTrue(content.contains("AssemblyFileVersion(\"2.2.0.5"));
        assertTrue(content.contains("AssemblyCopyright(\"MyCompany"));
	 }
	
	@Test
	public void testAssemblyInfoCppUpdate() throws InterruptedException, IOException, Exception {
		
		EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        envVars.put("PVERSION", "1.1.0.0");
        envVars.put("FVERSION", "2.2.0.5");
        envVars.put("COPYRIGHT", "MyCompany");
        j.jenkins.getGlobalNodeProperties().add(prop);
        FreeStyleProject project = j.createFreeStyleProject();
        
        project.getBuildersList().add(new TestBuilder() {
			
			@Override
			public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
					BuildListener listener) throws InterruptedException, IOException {
				
				build.getWorkspace().child("AssemblyInfo.cpp").write("using namespace System::Reflection;\n" +
						"\n" +
						"[assembly: AssemblyTitleAttribute(\"\")]\n" +
						"[assembly: AssemblyDescriptionAttribute(\"\")]\n" +
						"[assembly: AssemblyCompanyAttribute(\"\")]\n" +
						"[assembly: AssemblyFileVersionAttribute(\"12.13.24.56\")]\n" +
						"[assembly: AssemblyCopyrightAttribute(\"\")]\n" +
						"[assembly: AssemblyTrademarkAttribute(\"\")]\n" +
						"[assembly: AssemblyCultureAttribute(\"\")]\n" +
						"[assembly: AssemblyVersionAttribute(\"12.1.14.96\")]", "UTF-8");
				
				return true;
				
			}
		});
        
        VersionPatcherBuilder builder = new VersionPatcherBuilder("${WORKSPACE}", "AssemblyInfo.cpp", "${FVERSION}", "${PVERSION}", "${COPYRIGHT}");
        project.getBuildersList().add(builder);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        String content = build.getWorkspace().child("AssemblyInfo.cpp").readToString();
        assertTrue(content.contains("AssemblyVersionAttribute(\"1.1.0.0"));
        assertTrue(content.contains("AssemblyFileVersionAttribute(\"2.2.0.5"));
        assertTrue(content.contains("AssemblyCopyrightAttribute(\"MyCompany"));
	 }
}
