JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class

SRCDIR=src/ClubSimulation
BINDIR=bin

# Compile .java files into .class files in the specified directory
$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR) -cp $(BINDIR) -sourcepath $(SRCDIR) $<

# List of classes to compile
CLASSES=GridBlock \
	PeopleCounter \
	PeopleLocation \
	CounterDisplay \
	ClubView \
	ClubGrid \
	Clubgoer \
	ClubSimulation

# Transform class names into corresponding .class filenames
CLASS_FILES=$(CLASSES:%=$(BINDIR)/%.class)

# Default target to build all class files
default: $(CLASS_FILES)

# Clean up generated .class files
clean:
	rm -f $(BINDIR)/*.class

# Run the simulation
run: $(CLASS_FILES)
	$(JAVA) -cp $(BINDIR) clubSimulation.ClubSimulation $(totalPeople) $(gridX) $(gridY) $(capacity)