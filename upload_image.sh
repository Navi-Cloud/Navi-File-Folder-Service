#!/bin/bash
docker build . -t storage_tmp
docker tag storage_tmp kangdroid.azurecr.io/navistorage_runner:latest
docker push kangdroid.azurecr.io/navistorage_runner:latest