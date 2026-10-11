#!/usr/bin/env groovy
def GetSiebelObjectList () {
  def objectList= [
  "Applet": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Business Component": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Business Objec": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "View": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Screen": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Workflow": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Task": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Project": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Import Object": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Application": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Link": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Picklist": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Table": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Message Category": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Web Template": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Integration Object": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
  "Business Service": ["jobName": "DeployObjects", "propagateState": false, "quietTime": 5, "waitTime": true],
];
def objectList2= [
"Applet": ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "false"],
"Business Component": ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "true"],
];

def objectList3 = [
    [
  "JobName": "ObjectImport",
  "JobList": [
        [ "StepName": "APPLET", "StepDetails":  ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "false"]],
        [ "StepName": "BC", "StepDetails":  ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "false"]],
      ]
    ],
    [
  "JobName": "ObjectImport 2",
  "JobList": [
        [ "StepName": "IO", "StepDetails":  ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "false"]],
        [ "StepName": "BO", "StepDetails":  ["jobName": "DeployObjects", "propagateState": "false", "quietTime": "5", "waitTime": "false"]],
      ]
    ]
]

  return objectList3;
}
