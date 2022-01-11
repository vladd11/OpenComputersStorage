local component = require("component")
local sides = require("sides")
local robot = require("robot")

function update(id, side, positionX, positionY, positionZ)
    print("It's slow. Update.")
    print(id,side, positionX, positionY, positionZ)
    local file = io.open("/home/chests/" .. id, "w")
    file:write(tostring(positionX) .. " " .. tostring(positionY) .. " " .. tostring(positionZ) .. " " .. tostring(side) .. "\n")
    local pre = side
    if side == sides.left then
        robot.turnLeft()
        side = 3
    elseif side == sides.right then
        robot.turnRight()
        side = 3
    elseif side == sides.back then
        robot.turnAround()
        side = 3
    end
    for i = 1, 54 do
        local item = component.inventory_controller.getStackInSlot(side, i)
        if (item == nil) then
            file:write("minecraft:air" .. "\n")
        else
            file:write(item.name .. " " .. item.size .. " " .. tostring(item.hasTag) .. " " .. item.label .. "\n")
        end
    end
    if pre == sides.left then
        robot.turnRight()
    elseif pre == sides.right then
        robot.turnLeft()
    elseif pre == sides.back then
        robot.turnAround()
    end
    file:close()
end
