cd ../workspace/wcnmaster
call mvn package
move /y "<path to target folder>/Spectral-1.0.jar" "<path to new location>"
move /y "<path to target folder>/Elysian-1.0.jar"" "<path to new location>"
move /y "<path to target folder>/Divinity-1.0.jar"" "<path to new location>"