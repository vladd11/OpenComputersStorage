local component = require("component")
local robot = require("robot")

movement = {}

function movement.goTo(positionX, positionY, positionZ)
    local currentPositionX, currentPositionY, currentPositionZ = component.navigation.getPosition()
    currentPositionX, currentPositionY, currentPositionZ = math.floor(currentPositionX), math.floor(currentPositionY), math.floor(currentPositionZ)
    if currentPositionX == positionX and currentPositionY == positionY and currentPositionZ == positionZ then
        return;
    end

    if currentPositionZ == positionZ then
        moveX(positionX, currentPositionX)
        print(positionY, currentPositionY)
        moveY(positionY, currentPositionY)
    end
end

function movement.goBack()
    goTo(8, 4, 3)
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

return movement