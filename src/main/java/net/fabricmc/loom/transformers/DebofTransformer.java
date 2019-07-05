/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.transformers;

import net.fabricmc.loom.util.ProjectHolder;
import net.fabricmc.loom.util.ModProcessor;
import org.gradle.api.Project;
import org.gradle.api.artifacts.transform.*;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;

public abstract class DebofTransformer implements TransformAction<DebofTransformer.Parameters> {

	@PathSensitive(PathSensitivity.ABSOLUTE)
	@InputArtifact
	public abstract Provider<FileSystemLocation> getInput();

	@InputArtifactDependencies
	public abstract FileCollection getDependencies();

	@Override
	public void transform(TransformOutputs outputs) {
		Logger logger = LoggerFactory.getLogger(DebofTransformer.class);
		ConfigurableFileCollection mappingsFileCollection = getParameters().getMappings();
		File mappingsFile = mappingsFileCollection.iterator().next(); //.getAsFile().get();
		FileCollection dependencies = getDependencies();

		File inputFile = getInput().get().getAsFile();

		Project project =  ProjectHolder.getProject();

		logger.warn("Project: " + project.getDisplayName());
		logger.warn("Hello this seems to work!");
		logger.warn("using mappings: " + mappingsFile.toString());

//		if(true){
//			throw new RuntimeException("this works");
//		}

		try {
			String fileName = inputFile.getName();
			String nameWithoutExtension = fileName.substring(0, fileName.length() - 4);
			ModProcessor.remapJar2(inputFile, outputs.file(nameWithoutExtension + "-remapped.jar"), mappingsFile, project);
		} catch (IOException e) {
			throw new RuntimeException("Failed to remap " + inputFile.getName(), e);
		}


		logger.warn("remapping dependencies");
		for(File depFile : dependencies) {

			// TODO: add marker file in jar or similar
			if(depFile.getName().endsWith("-remapped.jar")) {
				logger.warn("skipping already remapped " + depFile);
				continue;
			}
			if (ZipUtil.containsEntry(depFile, "fabric.mod.json")) {
				try {
					String fileName = depFile.getName();
					String nameWithoutExtension = fileName.substring(0, fileName.length() - 4);
					ModProcessor.remapJar2(depFile, outputs.file(nameWithoutExtension + "-remapped.jar"), mappingsFile, project);
				} catch (IOException e) {
					throw new RuntimeException("Failed to remap " + depFile.getName(), e);
				}
			} else {
				logger.warn("skipping " + depFile);
			}
		}

//		outputs.file(getInput());
	}

	public interface Parameters extends TransformParameters {
		// TODO add ConfigurableFileCollection for mappings

		@InputFiles
		ConfigurableFileCollection getMappings();
	}

}
