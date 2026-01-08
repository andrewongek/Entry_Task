# format the code
format:
	mvn spotless:apply

# run the spotbugs
spotbugs:
	mvn spotbugs:check
