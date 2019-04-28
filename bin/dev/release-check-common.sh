#!/usr/bin/env bash

BASE_TMP_DIR="$(dirname $0)/../../build/"
DIST_RELEASE_DIR="build/distributions/"
DIST_JAR_DIR="build/libs/"

function change_dir {
	echo "Changing dir to $1 ..."
	if ! cd ${1}; then
		echo "Failed to cd to '${1}'"
		exit 1
	fi
	echo "Current directory: $(pwd)"
}

# create tmp subdir in the current directory and change to it
function create_subdir_n_cd {
	TMPDIR=$(mktemp -d --tmpdir="$(pwd)")
	echo "Created temp dir '${TMPDIR}'"
	change_dir "${TMPDIR}"
}

function assert_dir_exists {
	echo -n "Asserting '$1' is a directory ..."
	if ! test -d "$1"; then
		echo "File '$1' is missing or not a directory"
		exit 1
	fi
	echo OK
}

function assert_file_exists_regular {
	echo -n "Asserting '$1' is a regular file ..."
	if ! test -s "$1"; then
		echo "File '$1' is missing or not a regular file"
		exit 1
	fi
	echo OK
}

function assert_dir_NOT_exists {
	echo -n "Asserting '$1' is a directory ..."
	if test -d "$1"; then
		echo "Directory '$1' should not exist"
		exit 1
	fi
	echo OK
}

function assert_file_NOT_exists {
	echo -n "Asserting file '$1' does not exist ..."
	if test -e "$1"; then
		echo "File '$1' should not exist"
		exit 1
	fi
	echo OK
}

# extract archive
function extract_archive {
	if [ -z "$1" ]; then
		echo "No archive provided"
		exit 1
	fi

	if echo "$1" | grep "zip"; then
		unzip -q "$1"
	elif echo "$1" | grep "tar.gz"; then
		tar -xzf "$1"
	else
		echo "Archive '$1' is neither zip nor .tar.gz"
		exit 1
	fi
}

# extract archive and change into directory
function extract_archive_n_cd {
	extract_archive $1

	change_dir kieker-*
}