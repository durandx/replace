import groovy.json.JsonSlurper
import groovy.util.AntBuilder;

class Replace {
	static void main(String[] args) {
		def ant = new AntBuilder()
		def srcDir = ant.project.baseDir;
		println ant.project.baseDir
		
		def java = "**/*.java"
		def xml = "**/*.xml"
		
		if (args.length < 1) {
			println  "Usage : java -jar replace-1.0.0.jar replace-x.json"
			return;
		}
		
		def inputJSON = new JsonSlurper().parse(new File(args[0]))
		
		inputJSON.forEach { type, rep ->
			switch(type) {
				case "replace":
					rep.forEach { k, v ->
						println "Renommage $k => $v"
						ant.replace(token: k, value: v, summary: true) {
							fileset(dir: srcDir, includes: "$xml,$java")
						}
					}
					break;
				case "replaceregexp":
					rep.forEach { k, v ->
						println "Renommage $k => $v"
						ant.replaceregexp(flags:"gis", byline:false) {
							fileset(dir: srcDir, includes: "$xml,$java")
							regexp(pattern:k)
							substitution(expression:v)
						}
					}
					break;
				case "delete":
					rep.forEach { k ->
						println "Suppression de $k"
						ant.delete(dir: srcDir, includes: "**/$k")
						}
					break;
			}
		}
	}
}
