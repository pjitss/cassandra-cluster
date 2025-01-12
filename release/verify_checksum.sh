#!/bin/bash
# Author -

# Read the user inputs (filename and checksum value) from release.txt
read -r d1 d1_checksum < release.txt

# Initialize a flag to track matches
a=0

# Loop through each line in RELEASE_FILE.txt
while read -r line; do
  # Extract the filename and checksum from each line
  file_name=$(echo "${line}" | awk '{print $1}')
  file_checksum=$(echo "${line}" | awk '{print $2}')

  # Compare both the filename and checksum
  if [[ "${d1}" == "${file_name}" && "${d1_checksum}" == "${file_checksum}" ]]; then
    echo "[INFO] Filename and checksum match! File is valid for download."
    a=1
    break
  fi
done < RELEASE_FILE.txt

# Check the match status
if [[ $a -eq 1 ]]; then
  echo "[INFO] Validation successful. Proceeding with the download."
  exit 0
else
  echo "[ERR] Filename or checksum mismatch! Validation failed."
  exit 1
fi
