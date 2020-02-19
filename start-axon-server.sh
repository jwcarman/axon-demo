#!/bin/bash
docker run -d --name local-axon-server -p 8024:8024 -p 8124:8124 axoniq/axonserver