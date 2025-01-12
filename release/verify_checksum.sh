#!/bin/bash
# Author -

# Read the checksum value from release.txt
read -r d1_checksum < release.txt

# Debug: Print the input checksum
echo "[DEBUG] Input checksum from release.txt: ${d1_checksum}"

# Initialize a flag to track matches
a=0

# Loop through each line in RELEASE_FILE.txt
while read -r file_checksum; do
  # Debug: Print the checksum being checked
  echo "[DEBUG] Checking checksum: ${file_checksum}"

  # Compare the checksum
  if [[ "${d1_checksum}" == "${file_checksum}" ]]; then
    echo "[INFO] Checksum match! File is valid for download."
    a=1
    break
  fi
done < RELEASE_FILE.txt

# Check the match status
if [[ $a -eq 1 ]]; then
  echo "[INFO] Validation successful. Proceeding with the download."
  exit 0
else
  echo "[ERR] Checksum mismatch! Validation failed."
  exit 1
fi
