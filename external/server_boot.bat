@echo off
cd <path to server>

for /l %%x in (1, 1, 100) do (
		call <name of start bat file>
		echo Rebooting in 5 seconds!
		for /l %%y in (1, 1, 5) do (
			echo %%y
			call ping 192.0.2.2 -n 1 -w 1000 > nul
		)
)