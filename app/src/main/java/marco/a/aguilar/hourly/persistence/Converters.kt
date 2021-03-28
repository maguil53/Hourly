package marco.a.aguilar.hourly.persistence

import androidx.room.TypeConverter
import marco.a.aguilar.hourly.enums.BlockType
import marco.a.aguilar.hourly.enums.TaskType

class Converters {

    @TypeConverter fun blockTypeToInt(blockType: BlockType): Int = blockType.ordinal

    // https://stackoverflow.com/questions/57326789/how-to-save-enum-field-in-the-database-room
    @TypeConverter fun intToBlockType(blockInt: Int): BlockType = enumValues<BlockType>()[blockInt]

}