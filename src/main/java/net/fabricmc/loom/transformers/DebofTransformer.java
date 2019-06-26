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
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;
import java.io.IOException;

public abstract class DebofTransformer implements TransformAction<DebofTransformer.Parameters> {

	@PathSensitive(PathSensitivity.ABSOLUTE)
	@InputArtifact
	public abstract Provider<FileSystemLocation> getInput();

	@Override
	public void transform(TransformOutputs outputs) {
		ConfigurableFileCollection mappingsFileCollection = getParameters().getMappings();
		File mappingsFile = mappingsFileCollection.iterator().next(); //.getAsFile().get();

		File inputFile = getInput().get().getAsFile();

		Project project =  ProjectHolder.getProject();

		project.getLogger().lifecycle("Hello this seems to work!");
		project.getLogger().lifecycle("using mappings: " + mappingsFile.toString());

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

		outputs.file(getInput());
	}

	public interface Parameters extends TransformParameters {
		// TODO add ConfigurableFileCollection for mappings

		@InputFiles
		ConfigurableFileCollection getMappings();
	}

}