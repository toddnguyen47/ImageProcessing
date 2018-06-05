import os

src_path = './src/'
bin_path = './bin/'
javac_file = 'javac.txt'

dirlist = []

# If ./bin/ doesn't exist, create it
if (os.path.exists(bin_path) is False):
	os.mkdir(bin_path)

print('Enter in javac flags. Enter \"none" for no flags:')
flags = input('>>> ')

if flags.lower() == "none":
	flags = ""

for x in os.walk(src_path):
	cur_dir = x[0]
	for file in os.listdir(cur_dir):
		if ".java" in file: # check if the directory has a java file
			dirlist.append(cur_dir.replace('\\','/'))
			break
	
with open(javac_file, 'w') as file:
	if (flags != ""):
		file.write('javac -d bin ' + flags.strip() + " ")
	else:
		file.write('javac -d bin ')
		
	for dir in dirlist:
		file.write(dir + '/*.java ')
		
print("Successfully wrote javac command into " + javac_file)