#!/bin/bash
mkdir /tmp/sshfm >> /dev/null 2>> /dev/null < /dev/null
cp -Rv "##COPYFROM##" "##COPYTO##" >> /tmp/sshfm/sshfm_log_##TASKNAME##
echo SSH_FM_TASK_OK >> /tmp/sshfm/sshfm_log_##TASKNAME##
rm -f /tmp/sshfm/sshfm_##TASKNAME##.sh
