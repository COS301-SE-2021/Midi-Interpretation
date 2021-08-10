#!/bin/bash

echo "1. Please enter the directory where files will temporarily be stored: "
read MIDI_STORAGE_ROOT

echo "2. Please enter the repository url: "
read MIDI_URL

echo "2. Please enter the repository username: "
read MIDI_USER

echo "3. Please enter the repository password: "
read -s MIDI_PASS
echo


file_name="application.properties"
install_location="backend/src/main/resources"

base_directory="$PWD/.."
install_dir="$base_directory/$install_location"

prop_content="
# REPOSITORY

spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=$MIDI_URL
spring.datasource.username=$MIDI_USER
spring.datasource.password=$MIDI_PASS
logging.level.root=WARN

# REQUESTS

midisense.config.CROSS_ORIGIN=*

# FILE STORAGE

midisense.config.MIDI_STORAGE_ROOT=$MIDI_STORAGE_ROOT/

midisense.config.FILE_FORMAT=.mid

# MAX FILE SIZE

midisense.config.MAX_FILE_UPLOAD_SIZE=128
midisense.config.DELETE_UPON_INTERPRET=false

# AI PARAMETERS

midisense.config.MATRIX_WEIGHT_ROOT=$base_directory/backend/src/main/java/com/noxception/midisense/intelligence/structure/

# EXCEPTIONS

midisense.config.EMPTY_REQUEST_EXCEPTION_TEXT=No Request Made
midisense.config.FILE_TOO_LARGE_EXCEPTION_TEXT=Maximum File Size (\${midisense.config.MAX_FILE_UPLOAD_SIZE} Kb) Exceeded
midisense.config.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT=No File Matches Designator
midisense.config.FILE_ALREADY_EXISTS_EXCEPTION_TEXT=File Already Exists
midisense.config.EMPTY_FILE_EXCEPTION_TEXT=Empty File
midisense.config.FILE_SYSTEM_EXCEPTION_TEXT=File System Failure
midisense.config.MISSING_ANALYSIS_STRATEGY_EXCEPTION_TEXT = No AI classification strategy has been set

# PARSING

midisense.config.INVALID_MIDI_EXCEPTION_TEXT=MIDI Interpretation Failure
midisense.config.INVALID_TRACK_INDEX_EXCEPTION_TEXT=Track Index Out Of Bounds
midisense.config.SUCCESSFUL_PARSING_TEXT=Successfully Parsed MIDI File

#TESTING

midisense.config.MIDI_TESTING_ROOT=$base_directory/backend/src/test/java/com/noxception/midisense/testData/
midisense.config.MIDI_TESTING_FILE=\${midisense.config.MIDI_TESTING_ROOT}testFile.mid
midisense.config.MIDI_INVALID_TESTING_FILE=\${midisense.config.MIDI_TESTING_ROOT}invalidMidi.JPG
midisense.config.MIDI_TESTING_DESIGNATOR=3169d7ac-216a-4400-a530-36525d005fbe
midisense.config.MIDI_TESTING_TRACK_INDEX=0
"
set -o noclobber
echo "$prop_content" > "$install_dir/$file_name"


printf "\nFinished installation of properties.\nAlterations to configurations can be made by altering:\n[%s]\n" "$install_location/$file_name"
printf "Press <enter> to exit"
read -r finish