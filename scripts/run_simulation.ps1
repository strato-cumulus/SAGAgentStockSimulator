
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
    $agents_array.Add($agents_names[0] + "_" + $i + ":" + $player_class + "(" + $i + ")")
}

#strategy 2...
for ( $i = 0; $i -lt $args[1]; $i++) {
  #Write-Host $i  
}

#strategy 3...
for ( $i = 0; $i -lt $args[2]; $i++) {
  #Write-Host $i  
}

#$agents_array.Add("Banker:" + $banker_class + "(" + $i + ")")
#$agents_array.Add("Broker:" + $broker_class + "(" + $i + ")")

$agents_list = $agents_array -Join ';' 

java -cp $class_path jade.Boot -container -container-name $container_name -agents $agents_list

