﻿
$project_path=$PSScriptRoot+"\.."
$jade_path=$project_path+"\lib\jade.jar"
$out_path=$project_path+"\out\production\SAG"
$player_class="agent.PlayerAgent"
$broker_class="agent.BrokerAgent"
$banker_class="agent.BankAgent"
$class_path = $jade_path + ";" + $out_path + ";" + $project_path+"\lib\gson-2.8.1.jar"
$container_name = "Stock market"
$agents_array = [System.Collections.ArrayList]@()

$agents_names=@(
    "test1",
    "test2"
)

$agents_config=@(1,2)

#strategy 1...
for ( $i = 0; $i -lt $args[0]; $i++) {
    $agents_array.Add($agents_names[0] + "_" + $i + ":" + $player_class + "(CheapestBuy, 1500)")
}

	
$agents_array.Add("bank-0:" + $banker_class)
$agents_array.Add("broker-0:" + $broker_class)

$agents_list = $agents_array -Join ';' 

java -cp $class_path jade.Boot -container -container-name $container_name -agents $agents_list

