local component = require("component")

function update(id, checkSide, side, positionX, positionY, positionZ)
    print("It's slow. Update.")

    local file = io.open("/home/chests/" .. id, "w")
    if file == nil then
        print(id .. " file is nil")
        return
    end

    file:write(tostring(positionX) .. " " .. tostring(positionY) .. " " .. tostring(positionZ) .. " " .. tostring(side) .. "\n")
    for i = 1, 54 do
        local item = component.inventory_controller.getStackInSlot(checkSide, i)
        if (item == nil) then
            file:write("minecraft:air" .. "\n")
        else
            file:write(item.name .. " " .. item.size .. " " .. tostring(item.hasTag) .. " " .. item.label .. "\n")
        end
    end
    file:close()
end
