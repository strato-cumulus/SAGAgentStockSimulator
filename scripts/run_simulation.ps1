
$project_path=$PSScriptRoot+"\.."
$jade_path=$project_path+"\lib\jade.jar"
$out_path=$project_path+"\out\production\SAG"
$player_class="agent.PlayerAgent"
$broker_class="agent.BrokerAgent"
$banker_class="agent.BankAgent"
$class_path = $jade_path + ";" + $out_path + ";" + $project_path+"\lib\gson-2.8.1.jar"
$container_name = "Stock market"
$agents_array = [System.Collections.ArrayList]@()

$min_funds = 2000
$max_funds = 3000

$game_length = 100

$agents_names=@(
    "CheapestBuy",
    "InformationResponding",
    "OnFallBuying",
    "OnRiseBuying"
)

$agents_config=@(1,2)

#CheapestBuy
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[0] + "_" + $i + ":" + $player_class + "(CheapestBuy, " + $cash + ")")
}

#InformationResponding
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[1] + "_" + $i + ":" + $player_class + "(InformationResponding, " + $cash + ")")
}

#OnFallBuying
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[2] + "_" + $i + ":" + $player_class + "(OnFallBuying, " + $cash + ")")
}

#OnRiseBuying
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[3] + "_" + $i + ":" + $player_class + "(OnRiseBuying, " + $cash + ")")
}


$agents_array.Add("bank-0:" + $banker_class)
$agents_array.Add("broker-0:" + $broker_class + "(" + $game_length + ")")

$agents_list = $agents_array -Join ';' 

java -cp $class_path jade.Boot -container -container-name $container_name -agents $agents_list

