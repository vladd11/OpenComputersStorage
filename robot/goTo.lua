-- We assume that we are in point of extract
-- If no, you should move it there

local component = require("component")
local robot = require("robot")
local text = require("text")

require "update"

function checkSign()
    robot.turnRight()
    local value = component.sign.getValue()
    robot.turnLeft()
    robot.forward()
    return value
end

--[[function goTo(id)
	while true do
		local value = checkSign()
		if(value ~= nil) then
			value = text.trim(value)
			if(value ~= id) then goto continue end
			robot.turnRight()
			robot.forward()
			break
		end
		::continue::
	end
end--]]

function goTo(positionX, positionY, positionZ)
    local currentPositionX, currentPositionY, currentPositionZ = component.navigation.getPosition()
    currentPositionX, currentPositionY, currentPositionZ = math.floor(currentPositionX), math.floor(currentPositionY), math.floor(currentPositionZ)
    if currentPositionX == positionX and currentPositionY == positionY and currentPositionZ == positionZ then
        return;
    end

    if currentPositionZ == positionZ then
        moveX(positionX, currentPositionX)
        moveY(positionY, currentPositionY)
    end
end

function moveY(positionY, currentPositionY)
    local blocksToGo = positionY - currentPositionY
    if blocksToGo > 0 then
        for _ = 1, blocksToGo do
            robot.up()
        end
    else
        for _ = 1, math.abs(blocksToGo) do
            robot.down()
        end
    end
end

function moveX(positionX, currentPositionX)
    local blocksToGo = positionX - currentPositionX
    if blocksToGo > 0 then
        for _ = 1, blocksToGo do
            robot.forward()
        end
    else
        for _ = 1, math.abs(blocksToGo) do
            robot.back()
        end
    end
end