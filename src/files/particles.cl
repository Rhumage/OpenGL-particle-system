__constant int WORK_GROUP_SIZE = 128;
__constant float maxLifespan = 5.0f;
__constant float4 initVelocity = (15.0f, 0.0f, 0.0f, 10.0f);

float rand(float2 co){
    float x = sin(dot(co, (12.9898f, 78.233f))) * 43758.5453f;
    return x - floor(x);
}

kernel void particles(global float4 *pos, global float4 *vel, global float *lif, const float delta) {
    unsigned int xid = get_global_id(0);
    unsigned int yid = get_global_id(1);
    unsigned int id = xid * WORK_GROUP_SIZE + yid;

    vel[id][0] = vel[id][0] - vel[id][0] / 3 * delta;
    vel[id][1] = vel[id][1] - 9.81f * delta;

    pos[id][0] += vel[id][0] * delta;
    pos[id][1] += vel[id][1] * delta;
    pos[id][2] += vel[id][2] * delta;   
    lif[id] += delta;

    if (lif[id] > maxLifespan) {
        float2 seedX = (pos[id][0], pos[id][0]);
        float2 seedY = (pos[id][1], pos[id][1]);
        float2 seedZ = (pos[id][2], pos[id][2]);
        pos[id][0] = (rand(seedX) * 10.0f) - 25.0f;
        pos[id][2] = (rand(seedY) * 10.0f) - 5.0f;
        pos[id][1] = (rand(seedZ) * 7.0f) + 10.0f;
        lif[id] = 0;
        vel[id][0] = 15;
        vel[id][1] = 0;
        vel[id][2] = 0;
    }
}