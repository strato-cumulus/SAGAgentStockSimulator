﻿#start in first PowerShell, than in second start run_simulation.ps1

$project_path=$PSScriptRoot+"\.."
echo $project_path
$jade_path="\lib\jade.jar"
$path=$project_path + $jade_path

java -cp $path jade.Boot -gui

