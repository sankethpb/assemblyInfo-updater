/*
The MIT License (MIT)

Copyright (c) 2015 Sanketh P B

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.etas.jenkins.plugins.VersionPatcher;

import hudson.Extension;
import hudson.Launcher;
import hudson.PluginWrapper;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class VersionPatcherBuilder extends Builder {

	private final String searchPath;
	private final String fileExtension;
	private final String fileVersion;
	private final String productVersion;
	private final String copyrightString;

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public VersionPatcherBuilder(String searchPath, String fileExtension,
			String fileVersion, String productVersion, String copyrightString) {
		this.searchPath = searchPath;
		this.fileExtension = fileExtension;
		this.fileVersion = fileVersion;
		this.productVersion = productVersion;
		this.copyrightString = copyrightString;
	}

	public String getSearchPath() {
		return searchPath;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public String getCopyrightString() {
		return copyrightString;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {

	//	PluginWrapper pluginWrapper = Jenkins.getInstance().getPluginManager().whichPlugin(getClass());
		
	//	listener.getLogger().println(String.format("%s Version %s", pluginWrapper.getDisplayName(),pluginWrapper.getVersion()));
		

		String res_SearchPath = build.getEnvironment(listener).expand(
				searchPath);
		String res_fileVersion = build.getEnvironment(listener).expand(
				fileVersion);
		String res_productVersion = build.getEnvironment(listener).expand(
				productVersion);
		String copyrightStringTemp = copyrightString.replaceAll(
				"ES_CURRENT_YEAR",
				String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		String res_CopyrightString = build.getEnvironment(listener).expand(
				copyrightStringTemp);

		if (res_SearchPath == null || res_SearchPath.isEmpty()) {
			listener.getLogger().println("Error: Search path is not set.");
			return false;
		}
		
		listener.getLogger().println("Search Path: " + res_SearchPath);
		listener.getLogger().println("File Version: " + res_fileVersion);
		listener.getLogger().println("Product Version: " + res_productVersion);
		listener.getLogger().println("Copyright: " + res_CopyrightString);

		PatchFilesTask patchFiles = new PatchFilesTask(res_SearchPath,
				this.fileExtension, res_fileVersion, res_productVersion,
				res_CopyrightString, listener);

		return launcher.getChannel().call(patchFiles);

	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends Descriptor<Builder> {

		public DescriptorImpl() {
			load();
		}

		public FormValidation doCheckSearchPath(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set a search path.");

			return FormValidation.ok();
		}

		public FormValidation doCheckFileVersion(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set File Version.");

			return FormValidation.ok();
		}

		public FormValidation doCheckProductVersion(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set Product Version.");

			return FormValidation.ok();
		}

		public FormValidation doCheckCopyrightString(
				@QueryParameter String value) throws IOException,
				ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set copyright string.");

			return FormValidation.ok();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "AssemblyInfo Updater";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {
			save();
			return super.configure(req, formData);
		}

	}

}
