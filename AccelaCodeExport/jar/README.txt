Disclaimer and instructions for running our code export.

	Be aware the this exporter will create lots of files, You will need to have write permissions on your drive.

	I suggest running this from inside your favorite IDE (eclipse), look for the TODO: and update the code to
match your environment and execute it from in the IDE.  Some knowledge of java and the Accela data structure
is nice to have sense this is designed for our agency you may not want things our way.  Also this particular
distro is setup for SQL Server reconfiguration will be necessary to get it on Oracle.

	The code your will be generating includes: Standard Choices of type EMSE exported as .js files, Standard
Choices of type System Switch and Shared Drop Down exported as .json files, Event Scripts exported as .js
files, and Expression Builder Scripts exported as .js files. We have added ASI, ASIT, Conditions, and Fees
You can view the source code to see the queries used to generate the files.

Folder scheme //Parentheses surround folder names that are variable dependent on your Agencies' structure.
	C:\localfolder\
			(Agency)(Enviroment)
					Scripts
					StandardChoices
						EMSE
							(Event) [prefix of file name ending at " ", ":", or "_"]
						SystemSwitch
							(Module) [prefix of file name ending at " ", ":", or "_"]
						SharedDropDown
							(Module) [prefix of file name ending at " ", ":", or "_"]
					ExpressionBuilder [if expressions are used in agency]
						(Module) [prefix of name ending at " ", ":", or "_"]
							(Object) [value of REXPRESSION.R1_CHCKBOX_CODE]

[Any questions about the source code, jars, or bath files can be directed to jerjohnson@mt.gov.  Thanks!!]
