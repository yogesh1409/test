#!/usr/bin/env groovy

def GetEnvList() {
    def envList = ["DEV01", "DEV02", "DEV03", "DEV04", 
    "ST01", "ST02", "ST03", "ST04",
    "SIT01", "SIT02", "SIT03","SIT04","SIT05","SIT06","SIT07","SIT08","SIT09","SIT10",
    "CRR",
    "PT01",
    "PT02",
    "PREPROD",
    "TRAINING01",
    "TRAINING02",
    "PRODUCTION",
    "NN_PRODUCTION",
    "EK_PRODUCTION"
    ]
  return envList
}

def GetEnvironmentName(Environment) {
 def envList = [
   "DEV01": "Dev01", "DEV02": "Dev02", "DEV03": "Dev03", "DEV04": "Dev04",
   "ST01": "St01","ST02": "St02","ST03": "St03","ST04": "St04",
   "SIT01": "Sit01","SIT02": "Sit02","SIT03": "Sit03","SIT04": "Sit04","SIT05": "Sit05","SIT06": "Sit06","SIT07": "Sit07","SIT08": "Sit08","SIT09": "Sit09","SIT10": "Sit10",
   "CRR": "Crr",   
   "PT01": "Pt01",
   "PT02": "Pt02",
   "PREPROD": "Preprod",
   "TRAINING01": "Training01",
   "TRAINING02": "Training02",
   "PRODUCTION": "Production",
   "NN_PRODUCTION": "NN_Production",
   "EK_PRODUCTION": "EK_Production"
 ]
  return envList[Environment]
}

def GetSlaveIdentifier(Environment) {
   def envList = ["Dev01": "Dev", "Dev02": "Dev", "Dev03": "Dev", "Dev03": "Dev", "Dev04": "Dev", 
   "St01": 'SystemTest', "St02": 'SystemTest', "St03": 'SystemTest', "St04": 'SystemTest',
   "Sit01": "NPE","Sit02": "NPE","Sit03": "NPE","Sit04": "NPE","Sit05": "NPE","Sit06": "NPE","Sit07": "NPE","Sit08": "NPE","Sit09": "NPE","Sit10": "NPE",
   "Crr": "CRR",
   "Pt01": "PerfTest01",
   "Pt02": "PerfTest02",
   "Preprod": "PreProd",
   "Training01": "Training",
   "Training02": "Training",
   "Production": "Production",
   "NN_Production": "NN_Production",
   "EK_Production": "EK_Production"
   ]
  return envList[Environment]
}


def GetEnvironmentInfo(Environment) {
  def envInfo = [
     "Dev01": [
      "Type": "DEV",
      "Web" : ["VVSL40065"],
      "App" : ["VVSL50912"],
      "Gtw" : ["VVSL50912"],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "",
      "DB_Port": ""
    ],
     "Dev02": [
      "Type": "DEV",
      "Web" : ["VVSL40066"],
      "App" : ["VVSL50913"],
      "Gtw" : ["VVSL50913"],
      "DBHost" : "vvsl60179.vodafone.com.au",
      "DBServiceName" : "SEBL_DEV2.vodafone.com.au",
      "TNS_Name": "SBLDEV02",
      "DB_Port": "2521"
    ],
     "Dev03": [
      "Type": "DEV",
      "Web" : ["VVSL40067"],
      "App" : ["VVSL50914"],
      "Gtw" : ["VVSL50914"],
      "DBHost" : "vvsl60180.vodafone.com.au",
      "DBServiceName" : "SEBL_DEV3.vodafone.com.au",
      "TNS_Name": "SEBL_DEV3",
      "DB_Port": "2521"
    ],
     "Dev04": [
      "Type": "DEV",
      "Web" : ["VVSL40068"],
      "App" : ["VVSL50915"],
      "Gtw" : ["VVSL50915"],
      "DBHost" : "vvsl60180.vodafone.com.au",
      "DBServiceName" : "SEBL_DEV4.vodafone.com.au",
      "TNS_Name": "SBLDEV04",
      "DB_Port": "2521"
    ],
    "St01": [
      "Type": "ST",
      "Web" : ["VVSL40061","VVSL40062"],
      "App" : ["VVSL50917","VVSL50918"],
      "Gtw" : ["VVSL50917"],
      "DBHost" : "vvsl60177.vodafone.com.au",
      "DBServiceName" : "SEBL_ST1.VODAFONE.COM.AU",
      "TNS_Name": "SEBLST01",
      "DB_Port": "2521"
    ],
    "St02" : [
      "Type": "ST",
      "Web" : ["VVSL40062"],
      "App" : ["VVSL50918"],
      "Gtw" : ["VVSL50918"],
      "DBHost" : "vvsl60177.vodafone.com.au",
      "DBServiceName" : "SEBL_ST2.VODAFONE.COM.AU",
      "TNS_Name": "SEBLST02",
      "DB_Port": "2521"
    ],
     "St03": [
      "Type": "ST",
      "Web" : ["VVSL40063"],
      "App" : ["VVSL50919"],
      "Gtw" : ["VVSL50919"],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "",
      "DB_Port": ""
    ],
    "St04": [
      "Type": "ST",
      "Web" : ["VVSL40064"],
      "App" : ["VVSL50920"],
      "Gtw" : ["VVSL50920"],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "",
      "DB_Port": ""
    ],
    "Sit01": [
      "Type": "NPE",
      "Web" : ["vvsl40171"],
      "App" : ["vvsl50938"],
      "Gtw" : ["vvsl50938"],
      "DBHost" : "vvsl60211.vodafone.com.au",
      "DBServiceName" : "SEBL1I.vodafone.com.au",
      "TNS_Name": "SEBL_SIT01",
      "DB_Port": "2521"
    ],
    "Sit02": [
      "Type": "NPE",
      "Web" : ["vvsl40172"],
      "App" : ["vvsl50939"],
      "Gtw" : ["vvsl50939"],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "SEBL_SIT02",
      "DB_Port": ""
    ],
    "Sit03": [
      "Type": "NPE",
      "Web" : ["vvsl40173"],
      "App" : ["vvsl50940"],
      "Gtw" : ["vvsl50940"],
      "DBHost" : "vvsl60213.vodafone.com.au",
      "DBServiceName" : "sebl_sit3_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT03",
      "DB_Port": "2521"
    ],
    "Sit04": [
      "Type": "NPE",
      "Web" : ["vvsl40174"],
      "App" : ["vvsl50941"],
      "Gtw" : ["vvsl50941"],
      "DBHost" : "vvsl60214.vodafone.com.au",
      "DBServiceName" : "sebl_sit4_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT04",
      "DB_Port": "2521"
    ],
    "Sit05": [
      "Type": "NPE",
      "Web" : ["vvsl40175"],
      "App" : ["vvsl50942"],
      "Gtw" : ["vvsl50942"],
      "DBHost" : "vvsl60215.vodafone.com.au",
      "DBServiceName" : "sebl_sit5_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT05",
      "DB_Port": "2521"
    ],
    "Sit06": [
      "Type": "NPE",
      "Web" : ["vvsl40176"],
      "App" : ["vvsl50943"],
      "Gtw" : ["vvsl50943"],
      "DBHost" : "vvsl60216.vodafone.com.au",
      "DBServiceName" : "sebl_sit6_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT06",
      "DB_Port": "2521"
    ],
    "Sit07": [
      "Type": "NPE",
      "Web" : ["vvsl40177"],
      "App" : ["vvsl50944"],
      "Gtw" : ["vvsl50944"],
      "DBHost" : "vvsl60217.vodafone.com.au",
      "DBServiceName" : "sebl_sit7_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT07",
      "DB_Port": "2521"
    ],
    "Sit08": [
      "Type": "NPE",
      "Web" : ["vvsl40178"],
      "App" : ["vvsl50945"],
      "Gtw" : ["vvsl50945"],
      "DBHost" : "vvsl60218.vodafone.com.au",
      "DBServiceName" : "sebl_sit8_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT08",
      "DB_Port": "2521"
    ],
    "Sit09": [
      "Type": "NPE",
      "Web" : ["vvsl40179"],
      "App" : ["vvsl50946"],
      "Gtw" : ["vvsl50946"],
      "DBHost" : "vvsl60218.vodafone.com.au",
      "DBServiceName" : "sebl_sit9_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_SIT09",
      "DB_Port": "2521"
    ],
    "Sit10": [
      "Type": "NPE",
      "Web" : ["vvsl40180","vvsl40181"],
      "App" : ["vvsl50947","vvsl50948"],
      "Gtw" : ["vvsl50947","vvsl50948"],
      "DBHost" : "dbas-a-npe1-scn",
      "DBServiceName" : "sebl_sit10.vodafone.com.au",
      "TNS_Name": "SEBL_SIT10",
      "DB_Port": ""
    ],
    "Crr": [
      "Type": "CRR",
      "Web" : ["vvsl40185","vvsl40186"],
      "App" : ["vvsl50957","vvsl50958","vvsl50959","vvsl50960","vvsl50961"],
      "Gtw" : ["vvsl50955","vvsl50956"],
      "DBHost" : "dbas-crr-scn",
      "DBServiceName" : "sebl_crr.vodafone.com.au",
      "TNS_Name": "SEBL_CRR",
      "DB_Port": "2521"
    ],
    "Pt01": [
      "Type": "PT",
      "Web" : ["vvsl10451","vvsl10452","vvsl10453","vvsl10454","vvsl10455","vvsl10456","vvsl10457"],
      "App" : ["vvsl22451","vvsl22452","vvsl22453","vvsl22454","vvsl22455","vvsl22456","vvsl22457",
               "vvsl22458","vvsl22459","vvsl22460","vvsl22461","vvsl22462","vvsl22463","vvsl22464",
               "vvsl22465","vvsl22466","vvsl22467","vvsl22468"],
      "Gtw" : ["vvsl22449", "vvsl22450"],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "",
      "DB_Port": ""
    ],
    "Pt02": [
      "Type": "PT",
      "Web" : ["",""],
      "App" : ["",""],
      "Gtw" : [""],
      "DBHost" : "",
      "DBServiceName" : "",
      "TNS_Name": "",
      "DB_Port": ""
      ],
    "Training01": [
      "Type": "Training",
      "Web" : ["vvsl40182"],
      "App" : ["vvsl50949"],
      "Gtw" : ["vvsl50949"],
      "DBHost" : "vvsl60222",
      "DBServiceName" : "sebl_tr1_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_TRG01",
      "DB_Port": "2521"
    ],
    "Training02": [
      "Type": "Training",
      "Web" : ["vvsl40183"],
      "App" : ["vvsl50950"],
      "Gtw" : ["vvsl50950"],
      "DBHost" : "vvsl60222",
      "DBServiceName" : "sebl_tr2_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_TRG02",
      "DB_Port": "2521"
    ],
    "Preprod": [
      "Type": "Preprod",
      "Web" : ["vvsl40184"],
      "App" : ["vvsl50953"],
      "Gtw" : ["vvsl50952"],
      "DBHost" : "vvsl60223",
      "DBServiceName" : "sebl_ppd_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_PPD",
      "DB_Port": "2521"
    ],
    "Production": [
      "Type": "Prod",
      "Web" : ["vvsl10437", "vvsl10438", "vvsl10439", "vvsl10440",
               "vvsl10441", "vvsl10442", "vvsl10443", "vvsl10444", 
               "vvsl10445", "vvsl10446", "vvsl10447", "vvsl10448", 
               "vvsl10449", "vvsl10450"],
      "App" : ["vvsl22409", "vvsl22410", "vvsl22411", "vvsl22412",
               "vvsl22413", "vvsl22414", "vvsl22415", "vvsl22416",
               "vvsl22417", "vvsl22418", "vvsl22419", "vvsl22420",
               "vvsl22421", "vvsl22422", "vvsl22423", "vvsl22424",
               "vvsl22425", "vvsl22426", "vvsl22430", "vvsl22431", 
               "vvsl22432", "vvsl22433", "vvsl22434", "vvsl22435", 
               "vvsl22436", "vvsl22437", "vvsl22438", "vvsl22439", 
               "vvsl22440", "vvsl22441", "vvsl22442", "vvsl22443", 
               "vvsl22444", "vvsl22445", "vvsl22446", "vvsl22447"],
      "Gtw" : ["vvsl22407", "vvsl22408"],
      "DBHost" : "siebelcrm-sebldb-app",
      "DBServiceName" : "sebl_prd_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_PRD",
      "DB_Port": "2521"
    ],
    "NN_Production": [
      "Type": "Prod",
      "Web" : ["vvsl10437", "vvsl10438", "vvsl10439", "vvsl10440",
               "vvsl10441", "vvsl10442", "vvsl10443"],
      "App" : ["vvsl22409", "vvsl22410", "vvsl22411", "vvsl22412",
               "vvsl22413", "vvsl22414", "vvsl22415", "vvsl22416",
               "vvsl22417", "vvsl22418", "vvsl22419", "vvsl22420",
               "vvsl22421", "vvsl22422", "vvsl22423", "vvsl22424",
               "vvsl22425", "vvsl22426"],
      "Gtw" : ["vvsl22407", "vvsl22408"],
      "DBHost" : "siebelcrm-sebldb-app",
      "DBServiceName" : "sebl_prd_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_PRD",
      "DB_Port": "2521"
    ],
    "EK_Production": [
      "Type": "Prod",
      "Web" : ["vvsl10444", "vvsl10445", "vvsl10446", "vvsl10447",
               "vvsl10448", "vvsl10449", "vvsl10450"],
      "App" : ["vvsl22430", "vvsl22431", "vvsl22432", "vvsl22433",
               "vvsl22434", "vvsl22435", "vvsl22436", "vvsl22437",
               "vvsl22438", "vvsl22439", "vvsl22440", "vvsl22441",
               "vvsl22442", "vvsl22443", "vvsl22444", "vvsl22445",
               "vvsl22446", "vvsl22447"],
      "Gtw" : ["vvsl22428", "vvsl22429"],
      "DBHost" : "siebelcrm-sebldb-app",
      "DBServiceName" : "sebl_prd_sblapp.vodafone.com.au",
      "TNS_Name": "SEBL_PRD",
      "DB_Port": "2521"
    ]
  ]

  return envInfo[Environment]
}

def GetDevOpsBranchName(Identifier) {
    def envList = [
      "Dev": "dev_st", 
      "SystemTest":"dev_st",
      "Prod": "master", 
      "NPE": "dev_st", 
      "CRR": "dev_st", 
      "PerfTest01": "dev_st", 
      "PerfTest02": "dev_st", 
      "PreProd": "dev_st", 
      "Training01": "dev_st",
      "Training02": "dev_st",
      "Production":"dev_st",
      "EK_Production":"dev_st",
      "NN_Production":"dev_st"
      ]

    return envList[Identifier]
}


def GetCodeBranchName(Identifier) {
  def envList = [
    "Dev": "dev", 
    "SystemTest": "dev", 
    "NPE" :"dev", 
    "PerfTest01": "dev",
    "CRR": "dev" ,
    "Training": "dev",
    "PreProd": "dev",
    "Production": "dev",
    "EK_Production": "dev",
    "NN_Production": "dev"
    ]
  log.info("Repo Name " + envList[Identifier]);
  return envList[Identifier]
}
def GetRepositoryName(Type){
  def URL = [
    "Code"  : "https://github.com/VodafoneAustralia/vha_siebel_crm",
    "DevOps": "https://github.com/VodafoneAustralia/vha_siebel_devops"
  ]
  log.info("Repo Name "+URL[Type]);
  return URL[Type]
}

def GetRepositoryPath(Type){
   def PATH = [
    "Code": "github.com/VodafoneAustralia/vha_siebel_crm.git",
    "DevOps": "github.com/VodafoneAustralia/vha_siebel_devops.git"
  ]
 
  return PATH[Type]
}

